package ui.components;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LabelledInput extends VBox{
    Label label;
    TextField textField;
    public LabelledInput(String text1, double textSize1, String text2, double textSize2){
        label= new Label(text1);
        label.setFont(new Font(textSize1));
        label.setPadding(new Insets(2, 5, 0, 5));
        
        label.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(2),
            new Insets(2)
        )));
        
        label.setBorder(Border.EMPTY);
        
        getChildren().add(label);
        textField = new TextField();
        textField.setText(text2);
        textField.setFont(new Font(textSize2));

        textField.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(5),
            new Insets(2)
        )));
        
        textField.setBorder(Border.EMPTY);
        textField.setPadding(new Insets(0, 5, 2, 15));

        getChildren().add(textField);
        setMargin(label, new Insets(5));
        setMargin(textField, new Insets(5));

        setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(5),
            new Insets(2)
        )));

        setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0, 0, 5, 5, false),
                new BorderWidths(1, 0, 0, 0)
            )
        ));
    }

    public StringProperty textProperty(){
        return textField.textProperty();
    }

    public String getText(){
        return textField.getText();
    }
    public void setText(String text){
        textField.setText(text);
    }
    public void setTextFormatter(TextFormatter<String> formatter){
        textField.setTextFormatter(formatter);  
    }
    public void setOnAction(Runnable action){
        textField.setOnAction(e -> action.run());
        textField.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue) action.run();
        });
    }
}
