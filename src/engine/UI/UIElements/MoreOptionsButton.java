package engine.UI.UIElements;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MoreOptionsButton extends Button{
    String shownText;
    String hiddenText;
    boolean isShow = false;
    Node attached;
    public MoreOptionsButton(String shownText, String hiddenText, double size, Node attached){

        this.shownText = shownText;
        this.hiddenText = hiddenText;
        this.attached = attached;

        setBorder(
            Border.EMPTY
        );

        setBackground(
                new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(0, 0, 0, 15, false),
                        new Insets(0)
                    )
                )
            );
        setPadding(new Insets(5, 25, 5, 5));
        setFont(new Font(size));

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                isShow = !isShow;
                handleToggle();
            }
        });
        handleToggle();
    }
    public void handleToggle(){
        if(isShow){
            setText(shownText);
        }else{
            setText(hiddenText);
        }

        attached.setVisible(isShow);
        attached.setManaged(isShow);
    }
    public void setShown(boolean set){
        isShow = set;
        handleToggle();
    }
}
