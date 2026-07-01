package ui.controls;

import interaction.commands.EditElementCommand;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import parser.ParseException;
import plotting.GraphElementManager;
import plotting.plots.PolarPlot;
import ui.components.EquationInput;
import ui.components.LabelledInput;
import ui.components.MoreOptionsButton;

public class PolarPlotEditor extends AbstractPlotEditor{
    public String dependent = "r";
    public String independent = "\u03B8";

    private EquationInput box0;
    private LabelledInput box1;
    private LabelledInput box2;

    double minT = 0;
    double maxT = 50;

    private MoreOptionsButton advancedButton;
    private VBox advancedOptionsPanel;

    public PolarPlotEditor(GraphElementManager plotManager, PolarPlot plot){
        this.plotManager = plotManager;
        initialize();
        this.plot = plot;

        box0.setLabelText("r(\u03B8) = ");
        box0.setFieldText(plot.expression);
        
        colorChooser.setSelectedColor(plot.getColor());

        box1.setText(Double.toString(plot.tMin));
        box2.setText(Double.toString(plot.tMax));
        nameLabel.setText(plot.getName());
        addHandlers();
    }

    private void addHandlers(){
        super.attachListeners();
        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                newValue = newValue.replace("theta", "\u03B8");
                box0.setFieldText(newValue);
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

        box1.setTextFormatter(formatter1);
        box2.setTextFormatter(formatter2);

        Runnable action = () -> {
            minT = (Double.parseDouble(box1.getText().trim()));
            maxT = (Double.parseDouble(box2.getText().trim()));
            updateElement();
        };
        box1.setOnAction(action);
        box2.setOnAction(action);
    }
    protected void initialize(){  
        super.initialize();

        box0 = new EquationInput("r(\u03B8) = ", 14, "\u03B8");

        advancedOptionsPanel =new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        advancedButton = new MoreOptionsButton("\u25B6 Hide more options", "\u25BE Show more options", 9, advancedOptionsPanel);
        
        box1 = new LabelledInput("Minimum parameter value:", 9, "0", 14);
        box2 = new LabelledInput("Maximum parameter value:", 9, "50", 14);

        this.getChildren().add(box0);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box1);
        advancedOptionsPanel.getChildren().add(box2);
    }
    

    protected void updateElement() {
        String text = box0.getText();
        PolarPlot before = (PolarPlot) plot.copy();

        box0.highlightError(null);

        PolarPlot after;
        try {
            after = new PolarPlot(
                nameLabel.getText(),
                text,
                minT,
                maxT,
                colorChooser.getSelectedColor()
            );
        } catch (ParseException e) {
            box0.highlightError(e.getMessage());
            return;
        }

        if (before.equals(after)) return;

        undoManager.execute(
            new EditElementCommand(
                plot,
                before,
                after,
                plotManager
            )
        );
    }

    @Override
    public void updateValues(){   
        updatingFields = true;
        PolarPlot fPlot = (PolarPlot)plot;    
        colorChooser.setSelectedColor(plot.getColor());

        box0.setFieldText(fPlot.expression);

        if(!box1.isFocused()){
            box1.setText(Double.toString(fPlot.tMin));
        }

        if(!box2.isFocused()){
            box2.setText(Double.toString(fPlot.tMax));
        }
        minT = fPlot.tMin;
        maxT = fPlot.tMax;
        nameLabel.setText(plot.getName());
        updatingFields = false;
    }
}
