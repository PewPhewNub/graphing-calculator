package ui.components;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class EquationInput extends VBox{

    Label functionInputLabel;
    TextField functionInputField;
    HBox input;
    Label errorLabel;
    public EquationInput(String text1, double size, String text2){
        HBox input = new HBox();

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
        functionInputField.setTextFormatter(
            new TextFormatter<>(change -> {

                String text = change.getText();

                if(text.contains("theta")) {
                    change.setText(
                        text.replace("theta", "\u03B8")
                    );
                }

                return change;
            })
        );

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
        
        input.getChildren().add(functionInputLabel);
        input.getChildren().add(functionInputField);
        input.setPadding(new Insets(0, 25, 0, 25));

        getChildren().add(input);

        HBox.setHgrow(functionInputField, Priority.ALWAYS);
        functionInputField.setMaxWidth(Double.MAX_VALUE);

        functionInputLabel.maxWidthProperty().bind(
            input.widthProperty().multiply(1/3f)
        );

        errorLabel = new Label();
        errorLabel.setFont(new Font(9));
        errorLabel.setTextFill(Color.RED);

        getChildren().add(errorLabel);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setPadding(new Insets(0, 30, 0, 30));

        setPadding(new Insets(5, 0, 5, 0));
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
    public void highlightError(String text){
        if(text != null){
            functionInputField.setBorder(new Border(new BorderStroke(
                Color.RED,       // A soft, light gray color
                BorderStrokeStyle.SOLID,        // Solid line style
                new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
                new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
            )));
            functionInputLabel.setBorder(new Border(new BorderStroke(
                Color.RED,       // A soft, light gray color
                BorderStrokeStyle.SOLID,        // Solid line style
                new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
                new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
            )));

            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            errorLabel.setText(text);
        }else{ 
            functionInputField.setBorder(new Border(new BorderStroke(
                Color.rgb(220, 220, 220),       // A soft, light gray color
                BorderStrokeStyle.SOLID,        // Solid line style
                new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
                new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
            )));
            functionInputLabel.setBorder(new Border(new BorderStroke(
                Color.rgb(220, 220, 220),       // A soft, light gray color
                BorderStrokeStyle.SOLID,        // Solid line style
                new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
                new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
            )));
            
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

}
