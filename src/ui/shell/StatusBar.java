package ui.shell;

import interaction.InputController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StatusBar extends HBox{
    public Label mouseX;
    public Label mouseY;
    public Label currentScaleX;
    public Label currentScaleY;
    public Label view;
    public Label plotsCount;
    public StatusBar(){
        setPadding(new Insets(2, 10, 2, 10));
        setAlignment(Pos.CENTER_RIGHT);
        setBackground(
            new Background(
                new BackgroundFill(
                    Color.rgb(240, 240, 240),
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        );
        setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(2, 0, 2, 0)
                )
            )
        );

        setSpacing(20);
        mouseX = new Label("x: ----");
        getChildren().add(mouseX);
        mouseY = new Label("y: ----");
        getChildren().add(mouseY);
        currentScaleX = new Label("Scale X: ----/--");
        getChildren().add(currentScaleX);
        currentScaleY = new Label("Scale Y: ----/--");
        getChildren().add(currentScaleY);
        view = new Label("View : --x--");
        getChildren().add(view);
        plotsCount = new Label("Plots: --");
        getChildren().add(plotsCount);

        setHeight(30);
    }

    public void update(double mouseX, double mouseY, double scaleX, double scaleY, double width, double height, int plotCount){
        if(Double.isNaN(mouseX)){
            this.mouseX.setText("x: ----");
        }else{
            this.mouseX.setText("x: " + String.format("%.2f", mouseX));
        }
        if(Double.isNaN(mouseY)){
            this.mouseY.setText("y: ----");
        }else{
            this.mouseY.setText("y: " + String.format("%.2f", mouseY));
        }
        this.currentScaleX.setText("Scale X: " + String.format("%.2f", scaleX) + "u/ 1px");
        this.currentScaleY.setText("Y: " + String.format("%.2f", scaleY) + "u/ 1px");
        this.view.setText("View :" + (int)width + " x " + (int)(height));
        if(plotCount < 0){
            this.plotsCount.setText("Plots: --");
        }else{
            this.plotsCount.setText("Plots: " + plotCount);
        }
    }
}
