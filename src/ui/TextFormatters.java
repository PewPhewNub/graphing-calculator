package ui;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;

public class TextFormatters {
    public static UnaryOperator<TextFormatter.Change> greekLetters(){
        return change ->{
            change.setText(
                change.getText().replaceAll("theta", "\u03B8")
            );
            return change;
        };
    }
    
}
