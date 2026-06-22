package engine.UI.UIElements;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CloseButton extends Pane{
    public CloseButton(){
        Line l1 = new Line(8, 8, 22, 22);
        Line l2 = new Line(22, 8, 8, 22);

        l1.setStrokeWidth(2.5);
        l2.setStrokeWidth(2.5);
        l1.setStroke(Color.GRAY);
        l2.setStroke(Color.GRAY);

        getChildren().addAll(l1, l2);

        setPadding(new Insets(5, 5, 0, 0));
        
    }
}
