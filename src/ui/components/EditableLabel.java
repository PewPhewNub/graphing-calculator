package ui.components;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class EditableLabel extends StackPane{
    Label label;
    TextField textField;

    public EditableLabel(String text, double size){
        label = new Label(text);
        textField = new TextField(text);
        label.setFont(new Font(size));
        textField.setFont(new Font(size));

        getChildren().add(0, label);
        getChildren().add(1, textField);

        label.setVisible(true);
        label.setManaged(true);
 
        textField.setVisible(false);
        textField.setManaged(false);
        textField.setAlignment(Pos.CENTER);

        label.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                beginEditing();
            }
        });
        textField.setOnAction(e ->{
            endEditing();
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) endEditing();
        });
    }

    public void beginEditing(){
        textField.setText(label.getText());
        label.setVisible(false);
        label.setManaged(false);
 
        textField.setVisible(true);
        textField.setManaged(true);

        textField.requestFocus();
        textField.selectAll();
    }

    public void endEditing(){
        label.setText(textField.getText());
        
        label.setVisible(true);
        label.setManaged(true);
 
        textField.setVisible(false);
        textField.setManaged(false);
    }

    public void setText(String text){
        textField.setText(text);
        label.setText(text);
    }
    public String getText(){
        return label.getText();
    }

    public StringProperty textProperty(){
        return label.textProperty();
    }
}
