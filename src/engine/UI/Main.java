package engine.UI;

import engine.UI.plotEditor.FunctionPlotEditor;
import engine.UI.plotEditor.ODEPlotEditor;
import engine.UI.plotEditor.PolarPlotEditor;
import engine.plotting.plots.ParametricPlot;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    Renderer renderer;
    Scene scene;
    Graph graph;
    Graph currentGraph;
    StackPane graphStack;
    UIPanel ui;
    ToolBar toolBar;
    MenuBar menuBar;
    public static void main(String[] args){
        launch();
    }

    public void start(Stage stage) throws Exception{
        graph = new Graph(1200, 900);
        ui = new UIPanel(350, 900, graph);
        ui.setMinWidth(350);
        
        currentGraph = graph;
        graphStack = new StackPane();
        graphStack.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        graphStack.setMinSize(0, 100);

        graphStack.getChildren().add(graph);
        graph.setVisible(true);
        
        menuBar = new MenuBar();
        menuBar.setBackground(new Background(
            new BackgroundFill(
                Color.rgb(230, 230, 230),
                CornerRadii.EMPTY,
                Insets.EMPTY
            )
        ));
        menuBar.getMenus().add(new Menu("File"));
        menuBar.getMenus().add(new Menu("Plot"));
        menuBar.getMenus().add(new Menu("View"));

        toolBar = new ToolBar();
        
        //toolBar.setPrefWidth(50);
        //toolBar.setMaxWidth(50);
        toolBar.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        toolBar.getItems().add(new Button(){
                boolean isVisible = true;
            {
                setText(" ≡ ");
                setFont(new Font(20));
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
                        ui.setManaged(isVisible);
                        ui.setVisible(isVisible);
                    }
                });
            }
        });

        MenuButton addPlotButton = new MenuButton("Add Plot");

        MenuItem functionItem = new MenuItem("Function");
        functionItem.setOnAction(e ->{
            ui.getChildren().add(new FunctionPlotEditor(graph.plotManager));
        });
        MenuItem odeItem = new MenuItem("ODE");
        odeItem.setOnAction(e ->{
            ui.getChildren().add(new ODEPlotEditor(graph.plotManager));
        });
        MenuItem parametricItem = new MenuItem("Parametric");
        parametricItem.setOnAction(e ->{
            ui.getChildren().add(new PolarPlotEditor(graph.plotManager));
        });
        MenuItem polarItem = new MenuItem("Polar");
        polarItem.setOnAction(e ->{
            ui.getChildren().add(new PolarPlotEditor(graph.plotManager));
        });

        addPlotButton.getItems().addAll(
            functionItem,
            parametricItem,
            polarItem,
            odeItem
        );
        addPlotButton.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(3),
                new Insets(3)
            )
        ));
        addPlotButton.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGREY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(4),
                new BorderWidths(1)
            )
        ));

        toolBar.getItems().add(addPlotButton);
        toolBar.setPadding(new Insets(3, 3, 3, 3));

        BorderPane pane = new BorderPane();
        pane.setCenter(graphStack);

        VBox menuToolBarPanel = new VBox();
        menuToolBarPanel.getChildren().add(menuBar);
        menuToolBarPanel.getChildren().add(toolBar);

        pane.setTop(menuToolBarPanel);

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
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.show();

        //graph.plotManager.addPlot(new FunctionPlot("F1", f, Color.GREEN));
        //graph.plotManager.addPlot(new FunctionPlot("F2", x -> Math.sin(x)/x, Color.RED));
        //graph.plotManager.addPlot(new FunctionPlot("F3", x -> Math.sin(x - 2*Math.PI/3), Color.PURPLE));
        //graph.plotManager.addPlot(new ODEPlot("ODE1", (x,y) -> y, new Point(0, 1), Color.BLUE));
        //graph.plotManager.addPlot(new ParametricPlot("Para1", t -> Math.sin(t) * Math.exp(t/10), t -> Math.cos(t) * Math.exp(t/100), -50, 50, 50000, Color.BLACK));

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
