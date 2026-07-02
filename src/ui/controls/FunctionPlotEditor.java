package ui.controls;

import interaction.UndoManager;
import interaction.commands.EditElementCommand;
import javafx.scene.layout.VBox;
import parser.ParseException;
import plotting.GraphElementManager;
import plotting.plots.FunctionPlot;
import ui.components.EquationInput;
import ui.components.LabelledInput;
import ui.components.MoreOptionsButton;

public class FunctionPlotEditor extends AbstractPlotEditor{

    public String dependent = "y";
    public String independent = "x";

    private EquationInput box0;
    private LabelledInput box1;
    private LabelledInput box2;

    private MoreOptionsButton advancedButton;
    private VBox advancedOptionsPanel;

    public FunctionPlotEditor(GraphElementManager plotManager, UndoManager undoManager, FunctionPlot plot){
        super(undoManager);
        updatingFields = true;
        this.plotManager = plotManager;
        initialize();
        this.plot = plot;
        dependent = plot.dependent;
        independent = plot.independent;
        colorChooser.setSelectedColor(plot.getColor());

        box0.setLabelText(dependent + "(" + independent + ") = ");
        box0.setFieldText(plot.expression);
        nameLabel.setText(plot.getName());

        box1.setText(independent);
        box2.setText(dependent);

        attachListeners();
        updatingFields = false;
    }

    public void initialize(){
        super.initialize();

        box0 = new EquationInput(dependent + "(" + independent + ") = ", 14, independent);

        advancedOptionsPanel = new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        advancedButton = new MoreOptionsButton("\u25B6 Hide more options", "\u25BE Show more options", 9, advancedOptionsPanel);

        box1 = new LabelledInput("Independent Variable:", 9, "x", 14);
        box2 = new LabelledInput("Dependent Variable:", 9, "y", 14);

        this.getChildren().add(box0);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box1);
        advancedOptionsPanel.getChildren().add(box2);
    }

    protected void attachListeners(){
        super.attachListeners();

        box0.textProperty().addListener((obs, oldValue, newValue) -> {
            if(updatingFields)
                return;
            updateElement();
        });

        box1.textProperty().addListener((obs, oldValue, newValue) -> {
            independent = box1.getText();
            box0.setLabelText(dependent + "(" + independent + ") = ");
            if(updatingFields)
                return;
            updateElement();
        });

        box2.textProperty().addListener((obs, oldValue, newValue) -> {
            dependent = box2.getText();
            box0.setLabelText(dependent + "(" + independent + ") = ");
            if(updatingFields)
                return;
            updateElement();
        });
    }
    @Override
    protected void updateElement() {
        String text = box0.getText();
        FunctionPlot before = (FunctionPlot) plot.copy();

        box0.highlightError(null);

        FunctionPlot after;
        try {
            after = new FunctionPlot(
                nameLabel.getText(),
                text,
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
        FunctionPlot fPlot = (FunctionPlot)plot;    
        dependent = fPlot.dependent;
        independent = fPlot.independent;
        colorChooser.setSelectedColor(plot.getColor());

        box0.setLabelText(dependent + "(" + independent + ") = ");
        box0.setFieldText(fPlot.expression);

        box1.setText(independent);
        box2.setText(dependent);
        
        nameLabel.setText(plot.getName());
        updatingFields = false;
    }
}
