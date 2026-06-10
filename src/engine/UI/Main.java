package engine.UI;

import core.math.Core.Point;
import engine.plotting.FunctionPlot;
import engine.plotting.ODEPlot;
import engine.plotting.ParametricPlot;
import engine.plotting.PolarPlot;
import engine.rendering.Graph;
import engine.rendering.Renderer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    Renderer renderer;
    Scene scene;
    Graph graph;
    Graph currentGraph;
    StackPane graphStack;
    UIPanel ui;
    ToolBar toolBar;
    public static void main(String[] args){
        launch();
    }

    public void start(Stage stage){
        graph = new Graph(1200, 900);
        ui = new UIPanel(400, 900, graph);
        ui.setMinWidth(400);
        
        currentGraph = graph;
        graphStack = new StackPane();
        graphStack.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        graphStack.setMinSize(0, 100);

        graphStack.getChildren().add(graph);
        graph.setVisible(true);
        

        toolBar = new ToolBar();
        toolBar.setMinHeight(50);
        toolBar.setPrefHeight(50);
        toolBar.setMaxHeight(50);
        toolBar.setMinWidth(50);
        
        //toolBar.setPrefWidth(50);
        //toolBar.setMaxWidth(50);
        toolBar.getItems().add(new Button(){
                boolean isVisible = true;
            {
                setText(" ≡ ");
                setFont(new Font(30));
                setPadding(Insets.EMPTY);
                setBorder(Border.EMPTY);
                setBackground(Background.EMPTY);
                
                // 1. Pre-define a soft hover background (e.g., 8% opacity black)
                Background hoverBackground = new Background(
                    new BackgroundFill(Color.rgb(0, 0, 0, 0.08), CornerRadii.EMPTY, Insets.EMPTY)
                );

                // 2. Define the hover actions
                setOnMouseEntered(e -> {
                    setBackground(hoverBackground);
                    setCursor(Cursor.HAND);
                });

                setOnMouseExited(e -> {
                    setBackground(Background.EMPTY);
                    setCursor(Cursor.DEFAULT);
                });

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e){
                        isVisible = !isVisible;
                        ui.setVisible(isVisible);
                        ui.setManaged(isVisible);
                    }
                });
            }
        });
        
        BorderPane pane = new BorderPane();
        pane.setCenter(graphStack);
        pane.setTop(toolBar);
        pane.setLeft(ui);

        Border thinBorder = new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            CornerRadii.EMPTY,              // Perfectly square corners
            new BorderWidths(1)             // 1-pixel thickness
        ));
        toolBar.setBorder(thinBorder);
        ui.setBorder(thinBorder);
        //graphStack.setBorder(thinBorder);

        scene = new Scene(pane, 1600, 900);
        stage.setScene(scene);
        stage.setTitle("Almost Desmos");
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.show();

        graph.plotManager.addPlot(new FunctionPlot("F1", x -> 1/x, Color.GREEN));
        //graph.plotManager.addPlot(new FunctionPlot("F2", x -> Math.sin(x)/x, Color.RED));
        //graph.plotManager.addPlot(new FunctionPlot("F3", x -> Math.sin(x - 2*Math.PI/3), Color.PURPLE));
        graph.plotManager.addPlot(new ODEPlot("ODE1", (x,y) -> -1/(x*x), new Point(2, .5), Color.BLUE));
        //graph.plotManager.addPlot(new ParametricPlot("Para1", t -> Math.sin(t) * Math.exp(t/10), t -> Math.cos(t) * Math.exp(t/100), -50, 50, 50000, Color.BLACK));
        //graph.plotManager.addPlot(new PolarPlot("Polar1", t -> 1 + t, -50, 50, 50000, Color.ORANGE));

        for(Node i : graphStack.getChildren()){
            if(i instanceof Graph){  
                ((Graph)i).widthProperty().bind(graphStack.widthProperty());
                ((Graph)i).heightProperty().bind(graphStack.heightProperty());
            }
        }
        new AnimationTimer() {
            public void handle(long arg0){
                currentGraph.update();
                currentGraph.render();
            }
        }.start();
    }
}
