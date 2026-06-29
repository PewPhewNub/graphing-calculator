package ui.components;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class ToolTip extends Tooltip{
    public ToolTip(String text){
        super(text);

        setFont(new Font(8));

        setStyle("""
            -fx-background-color: #FFFFFF;
            -fx-background-radius: 1px;
            -fx-text-fill: #202020;
            -fx-font-size: 12px;
            -fx-padding: 0px 3px;
            -fx-border-color: #202020;
            -fx-border-width: 1px;
            -fx-border-radius: 1px;
            -fx-effect: none;
            """
        );
                
        setShowDelay(Duration.millis(300));
        setShowDuration(Duration.seconds(3));
        setHideDelay(Duration.millis(100));
    }
}
