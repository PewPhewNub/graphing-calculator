package io.github.pewphewnub.ui.shell;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.paint.Color;

public class StatusBar extends HBox{
    @FXML public Label mouseX;
    @FXML public Label mouseY;
    @FXML public Label currentScaleX;
    @FXML public Label currentScaleY;
    @FXML public Label view;
    @FXML public Label plotsCount;
    public StatusBar(){
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/io/github/pewphewnub/ui/shell/fxml/StatusBar.fxml")
        );
        loader.setRoot(this);
        loader.setController(this);
            
        try{
            loader.load();
        }catch(IOException e){
            throw new IllegalStateException(
                "Failed to load status bar", e
            );
        }

        getStylesheets().add(
            getClass().getResource(
                "/io/github/pewphewnub/ui/shell/css/StatusBar.css"
            ).toExternalForm()
        );
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
