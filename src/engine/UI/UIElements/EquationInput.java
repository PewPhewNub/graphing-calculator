package engine.UI.UIElements;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class EquationInput extends HBox{

    Label functionInputLabel;
    TextField functionInputField;
    public EquationInput(String text1, double size, String text2){
        functionInputLabel = new Label(text1);
        functionInputLabel.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
        )));
        functionInputLabel.setFont(new Font(size));
        functionInputLabel.setTextAlignment(TextAlignment.LEFT);
        functionInputLabel.setPadding(new Insets(5,0,5,15));
        functionInputLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);

        functionInputField = new TextField(text2);
        functionInputField.setFont(new Font(size));
        functionInputField.setAlignment(Pos.CENTER_LEFT);
        
        functionInputField.setTextFormatter(new TextFormatter<>(change ->{
            String text = change.getText();
            if(text.equals("(")) text = "()";
            change.setText(text);
            return change;
        }));
        functionInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains("theta")) {
                // Use Platform.runLater to avoid conflicts with the ongoing text update
                Platform.runLater(() -> {
                    int caretPosition = functionInputField.getCaretPosition();
                    
                    // Replace the text
                    String replaced = newValue.replace("theta", "\u03B8");
                    functionInputField.setText(replaced);
                    
                    // Adjust caret position so it doesn't jump to the beginning
                    functionInputField.positionCaret(caretPosition - 4); 
                });
            }
        });

        functionInputField.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        functionInputField.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
            new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
        )));
        functionInputField.setPadding(new Insets(5,0,5,0));
        
        getChildren().add(functionInputLabel);
        getChildren().add(functionInputField);
        setPadding(new Insets(5, 25, 5, 25));

        HBox.setHgrow(functionInputField, Priority.ALWAYS);
        functionInputField.setMaxWidth(Double.MAX_VALUE);

        functionInputLabel.maxWidthProperty().bind(
            widthProperty().multiply(1/3f)
        );
    }

    public StringProperty textProperty(){
        return functionInputField.textProperty();
    }

    public void setLabelText(String text){
        functionInputLabel.setText(text);   
    }
    
    public String getText(){   
        return functionInputField.getText();
    }

    public TextField getField(){
        return functionInputField;
    }
    public void setFieldText(String text){
        functionInputField.setText(text);   
    }
}
