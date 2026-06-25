package interaction.commands;

import javafx.scene.control.TextField;

public class FieldChangeCommand implements Command{
    private TextField field;
    private String oldValue;
    private String newValue;

    public FieldChangeCommand(TextField field, String oldValue, String newValue) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    @Override
    public void execute() {
        field.setText(newValue);
    }
    @Override
    public void undo() {
        field.setText(oldValue);    
    }
}
