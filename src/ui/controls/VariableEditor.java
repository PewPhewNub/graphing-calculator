package ui.controls;

import interaction.commands.EditElementCommand;
import interaction.commands.RemoveElementCommand;
import javafx.scene.control.TextFormatter;
import plotting.GraphElementManager;
import plotting.Variable;
import ui.components.LabelledInput;

public class VariableEditor extends AbstractEditor{
    public Variable variable;
    public GraphElementManager variableManager;
    public LabelledInput input;

    public VariableEditor(GraphElementManager variableManager, Variable variable){
        this.variableManager = variableManager;
        this.variable = variable;
        initialize();
        nameLabel.setText(variable.getName());
        
        setOnMouseClicked(e -> {
            variableManager.setSelectedElement(variable);
        });

        focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue) variableManager.setSelectedElement(null);
        });
    }
    public double getValue(){
        return Double.parseDouble(input.getText());
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
        
        this.input = new LabelledInput("Value", 9, "1", 14);
        getChildren().add(input);

        nameLabel.textProperty().addListener((obs, oldV, newV) -> {
            if(!updatingFields)
            updateElement();
        });

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
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

        input.setTextFormatter(formatter);

        Runnable action = () -> {
            updateElement();
        };
        input.setOnAction(action);
    }
    @Override
    public void updateValues() {
        updatingFields = true;
        nameLabel.setText(variable.getName());
        input.setText("" + variable.getValue());
        updatingFields = false;
    }
    public void updateElement(){
        String text = input.getText();
        try{
            double value = Double.parseDouble(text);
            
            Variable newVariable = new Variable(nameLabel.getText());
            newVariable.setValue(text);

            if(variable.equals(newVariable)) return;

            undoManager.execute(
                new EditElementCommand(variable, variable.copy(), newVariable, variableManager)
            );
        }catch(Exception e){
            return;
        }
    }
}
