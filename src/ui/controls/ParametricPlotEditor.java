package ui.controls;

import java.util.Set;

import interaction.commands.EditElementCommand;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import parser.ParseException;
import plotting.GraphElementManager;
import plotting.plots.ParametricPlot;
import plotting.plots.PlotGenerator;
import ui.components.EquationInput;
import ui.components.LabelledInput;
import ui.components.MoreOptionsButton;

public class ParametricPlotEditor extends AbstractPlotEditor{

    private EquationInput box0;
    private EquationInput box1;
    private LabelledInput box2;
    private LabelledInput box3;

    public String dependent1 = "x";
    public String dependent2 = "y";
    public String independent = "t";

    double minT = 0;
    double maxT = 50;

    private MoreOptionsButton advancedButton;
    private VBox advancedOptionsPanel;
    
    private boolean updatingFields = false;

    public ParametricPlotEditor(GraphElementManager plotManager, ParametricPlot plot){
        updatingFields = true;
        this.plotManager = plotManager;
        initialize();
        this.plot = plot;

        box0.setLabelText("x(t) = ");
        box0.setFieldText(plot.expression1);
        box1.setLabelText("y(t) = ");
        box1.setFieldText(plot.expression2);
        
        colorChooser.setSelectedColor(plot.getColor());

        box2.setText(Double.toString(plot.tMin));
        box3.setText(Double.toString(plot.tMax));
        nameLabel.setText(plot.getName());
        attachListeners();
        updatingFields = false;
    }

    protected void initialize(){
        super.initialize();
        box0 = new EquationInput("x(t) = ", 14, "2t");
        box1 = new EquationInput("y(t) = ", 14, "t*t");
        
        advancedOptionsPanel = new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        advancedButton = new MoreOptionsButton("\u25B6 Hide more options", "\u25BE Show more options", 9, advancedOptionsPanel);
        
        box2 = new LabelledInput("Minimum parameter value:", 9, "0", 14);
        box3 = new LabelledInput("Maximum parameter value:", 9, "50", 14);

        this.getChildren().add(box0);
        this.getChildren().add(box1);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box2);
        advancedOptionsPanel.getChildren().add(box3);
    }

    protected void attachListeners(){
        super.attachListeners();
        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                if(updatingFields)
                return;
                updateElement();
            }
        );
        box1.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                if(updatingFields)
                return;
                updateElement();
            }
        );
        TextFormatter<String> formatter1 = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow intermediate editing states
            if (newText.isEmpty()
                    || newText.equals("-")
                    || newText.equals(".")
                    || newText.equals("-.")) {
                return change;
            }

            try {
                Double.parseDouble(newText);
                return change;
            } catch (NumberFormatException e) {
                return null; // reject the edit
            }
        });
        TextFormatter<String> formatter2 = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow intermediate editing states
            if (newText.isEmpty()
                    || newText.equals("-")
                    || newText.equals(".")
                    || newText.equals("-.")) {
                return change;
            }

            try {
                Double.parseDouble(newText);
                return change;
            } catch (NumberFormatException e) {
                return null; // reject the edit
            }
        });

        box2.setTextFormatter(formatter1);
        box3.setTextFormatter(formatter2);

        Runnable action = () -> {
            minT = (Double.parseDouble(box2.getText().trim()));
            maxT = (Double.parseDouble(box3.getText().trim()));
            updateElement();
        };
        box2.setOnAction(action);
        box3.setOnAction(action);
    }

    @Override
    protected void updateElement() {
        String text1 = box0.getText();
        String text2 = box1.getText();

        ParametricPlot before = (ParametricPlot) plot.copy();

        box0.highlightError(null);
        box1.highlightError(null);

        boolean failed = false;

        try {
            PlotGenerator.generateDefinition(
                text1,
                "x",
                Set.of("t")
            );
        } catch (ParseException e) {
            box0.highlightError(e.getMessage());
            failed = true;
        }

        try {
            PlotGenerator.generateDefinition(
                text2,
                "y",
                Set.of("t")
            );
        } catch (ParseException e) {
            box1.highlightError(e.getMessage());
            failed = true;
        }

        if(failed) return;

        ParametricPlot after;
        try {
            after = new ParametricPlot(
                nameLabel.getText(),
                text1,
                text2,
                minT,
                maxT,
                colorChooser.getSelectedColor()
            );
        } catch (ParseException e) {
            return;
        }

        if(before.equals(after)) return;

        undoManager.execute(
            new EditElementCommand(
                plot,
                before,
                after,
                plotManager
            )
        );
    }
    public void updateValues(){   
        updatingFields = true;
        ParametricPlot fPlot = (ParametricPlot)plot;  
        colorChooser.setSelectedColor(plot.getColor());

        box0.setFieldText(fPlot.expression1);
        box1.setFieldText(fPlot.expression2);

        box2.setText(Double.toString(fPlot.tMin));
        box3.setText(Double.toString(fPlot.tMax));
        minT = fPlot.tMin;
        maxT = fPlot.tMax;
        nameLabel.setText(plot.getName());
        updatingFields = false;
    }
}
