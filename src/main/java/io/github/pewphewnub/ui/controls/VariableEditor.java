package io.github.pewphewnub.ui.controls;

import io.github.pewphewnub.interaction.UndoManager;
import io.github.pewphewnub.interaction.commands.EditElementCommand;
import io.github.pewphewnub.interaction.commands.RemoveElementCommand;
import io.github.pewphewnub.plotting.GraphElementManager;
import io.github.pewphewnub.plotting.Variable;
import io.github.pewphewnub.ui.components.AdjustableSlider;

public class VariableEditor extends AbstractEditor{
    public Variable variable;
    public GraphElementManager variableManager;
    public AdjustableSlider slider;

    public VariableEditor(GraphElementManager variableManager, UndoManager undoManager, Variable variable){
        super(undoManager);
        this.variableManager = variableManager;
        this.variable = variable;
        initialize();
        nameLabel.setText("Variable");
        this.slider = new AdjustableSlider(variable.getName(), variable.getMin(), variable.getMax(), variable.getStep(), variable.getValue());
        
        getChildren().add(slider);
        setOnMouseClicked(e -> {
            variableManager.setSelectedElement(variable);
        });

        focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue) variableManager.setSelectedElement(null);
        });
        slider.setOnValueChanged(() -> updateElement());
    }
    public double getValue(){
        return slider.getValue();
    }

    public void close(){
        undoManager.execute(
            new RemoveElementCommand(variable, variableManager)
        );
    }

    @Override
    protected void initialize() {
        super.initialize();
        colorChooser.setVisible(false);


        nameLabel.textProperty().addListener((obs, oldV, newV) -> {
            if(!updatingFields)
            updateElement();
        });
    }
    @Override
    public void updateValues() {
        updatingFields = true;
        nameLabel.setText("");

        slider.setText("" + variable.getName());
        slider.setMin(variable.getMin());
        slider.setMax(variable.getMax());
        slider.setValue(variable.getValue());
        updatingFields = false;
    }
    public void updateElement(){
        try{
            Variable newVariable = new Variable(
                slider.getText(),
                slider.getValue(),
                slider.getMin(),
                slider.getMax(),
                variable.getStep()
            );

            if(variable.equals(newVariable)) return;

            undoManager.execute(
                new EditElementCommand(variable, variable.copy(), newVariable, variableManager)
            );
        }catch(Exception e){
            return;
        }
    }
}
