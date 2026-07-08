package ui.controls;

import interaction.UndoManager;
import interaction.commands.EditElementCommand;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import math.Point;
import parser.ParseException;
import plotting.GraphElementManager;
import plotting.plots.ODEPlot;
import ui.components.EquationInput;
import ui.components.LabelledInput;
import ui.components.MoreOptionsButton;

public class ODEPlotEditor extends AbstractPlotEditor{

    public String dependent = "y";
    public String independent = "x";

    private EquationInput box0;
    private LabelledInput box1;
    private LabelledInput box2;

    private MoreOptionsButton advancedButton;
    private VBox advancedOptionsPanel;

    private Button generate;
    private CheckBox autoGenerate;
    private CheckBox slopeField;

    public ODEPlotEditor(GraphElementManager plotManager, UndoManager undoManager, ODEPlot plot){
        super(undoManager);
        this.plotManager = plotManager;
        initialize();
        this.plot = plot;

        box0.setLabelText("dy/dx = ");
        box0.setFieldText(plot.expression);

        box1.setText(plot.getInitial().x + "");
        box2.setText(plot.getInitial().y + "");
        addHandlers();
    }

    protected void initialize(){  
        super.initialize();

        box0 = new EquationInput("dy/dx = ", 14, "y");

        generate = new Button("Generate Solution");
        generate.setFont(new Font(10));
        generate.setBackground(new Background(
            new BackgroundFill(
                Color.TRANSPARENT,
                new CornerRadii(3),
                new Insets(0)
            )));
        generate.setBorder(Border.EMPTY);
        generate.setOnAction(e -> {
            try {
                updateElement();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });

        generate.setPadding(new Insets(5, 15, 5, 15));


        advancedOptionsPanel = new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        advancedButton = new MoreOptionsButton("\u25B6 Hide more options", "\u25BE Show more options", 9, advancedOptionsPanel);
        
        box1 = new LabelledInput("Initial Point x:", 9, "0", 14);
        box2 = new LabelledInput("Initial Point y:", 9, "1", 14);

        this.getChildren().add(box0);
        this.getChildren().add(generate);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box1);
        advancedOptionsPanel.getChildren().add(box2);

        autoGenerate = new CheckBox("Auto-Generate Solution upon loading chunks");
        autoGenerate.setFont(new Font(12));
        autoGenerate.setSelected(false);
        autoGenerate.setOnAction(e ->{
            if(plot != null) ((ODEPlot)plot).setAutoGenerate(autoGenerate.isSelected());
        });
        autoGenerate.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        autoGenerate.setPadding(new Insets(5, 28, 5, 25));
        advancedOptionsPanel.getChildren().add(autoGenerate);
        
        slopeField = new CheckBox("Show Slope Fields");
        slopeField.setFont(new Font(12));
        slopeField.setSelected(false);
        slopeField.setOnAction(e ->{
            if(plot != null) ((ODEPlot)plot).setShowSlopeField(slopeField.isSelected());
        });
        slopeField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        slopeField.setPadding(new Insets(5, 28, 5, 25));
        advancedOptionsPanel.getChildren().add(slopeField);
    }

    private void addHandlers(){
        super.attachListeners();
        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                newValue = newValue.replace("theta", "\u03B8");
                box0.setFieldText(newValue);
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

        slopeField.setOnAction(e -> updateElement());
    }

    @Override
    protected void updateElement() {
        ODEPlot before = (ODEPlot) plot.copy();

        box0.highlightError(null);

        ODEPlot after;
        try {
            after = new ODEPlot(
                nameLabel.getText(),
                box0.getText(),
                new Point(
                    Double.parseDouble(box1.getText().trim()), 
                    Double.parseDouble(box2.getText().trim())),
                colorChooser.getSelectedColor(),
                slopeField.isSelected()
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
        ODEPlot fPlot = (ODEPlot)plot;    
        colorChooser.setSelectedColor(plot.getColor());

        box0.setFieldText(fPlot.expression);

        if(!box1.isFocused()){
            box1.setText(Double.toString(fPlot.getInitial().x));
        }

        if(!box2.isFocused()){
            box2.setText(Double.toString(fPlot.getInitial().y));
        }

        nameLabel.setText(plot.getName());
        updatingFields = false;
    }
}
