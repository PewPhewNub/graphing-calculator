package engine.UI;

import core.math.Core.Point;
import engine.plotting.plots.VectorFieldPlot;
import engine.rendering.camera.CameraIntent;
import engine.rendering.core.Renderer;
import engine.rendering.graph.Graph;
import engine.scene.FunctionGraphScene;
import engine.scene.GraphScene;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    Renderer renderer;
    Scene scene;
    GraphScene currentScene;
    StackPane graphStack;
    UIPanel ui;
    HBox toolBar;
    MenuBar menuBar;
    BorderPane graphControls;
    HBox modeControls;
    HBox viewControls;
    HBox renderControls;
    VBox mainGraphPane;
    ComboBox<GraphType> modeType;
    Graph graph;
    public static void main(String[] args){
        launch();
    }

    public void start(Stage stage) throws Exception{
        currentScene = new FunctionGraphScene(1200, 900);
        initializeMenus();
        initializePlotControls();

        graphControls = new BorderPane();

        initializeModeControls();
        initializeViewControls();
        initializeRenderControls();
        
        graphStack = new StackPane();
        graphStack.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        graphStack.setMinSize(0, 100);

        graphStack.getChildren().add(currentScene.getGraph());
        currentScene.getGraph().setVisible(true);
        graph = currentScene.getGraph();
        
        toolBar = new HBox();
        HBox.setHgrow(graphControls, Priority.ALWAYS);
        graphControls.setMaxWidth(Double.MAX_VALUE);

        toolBar.getChildren().add(graphControls);
        toolBar.setBackground(new Background(new BackgroundFill(Color.rgb(250,250,250), new CornerRadii(0), new Insets(2, 2, 2, 2))));
        toolBar.setPadding(new Insets(3));
        BorderPane pane = new BorderPane();
        pane.setCenter(graphStack);

        VBox menuToolBarPanel = new VBox();
        menuToolBarPanel.getChildren().add(menuBar);
        
        mainGraphPane = new VBox();
        mainGraphPane.setFillWidth(true);
        mainGraphPane.getChildren().add(toolBar);
        mainGraphPane.getChildren().add(graphStack);
        VBox.setVgrow(graphStack, Priority.ALWAYS);
        toolBar.setMaxWidth(Double.MAX_VALUE);
        graphStack.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(graphStack, Priority.ALWAYS);

        pane.setTop(menuToolBarPanel);
        pane.setLeft(ui);
        pane.setCenter(mainGraphPane);

        Border thinBorder = new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            CornerRadii.EMPTY,              // Perfectly square corners
            new BorderWidths(2, 2, 2, 2)             // 1-pixel thickness
        ));
        toolBar.setBorder(thinBorder);
        
        //graphStack.setBorder(thinBorder);

        scene = new Scene(pane, 1600, 900);
        stage.setScene(scene);
        stage.setTitle("Almost Desmos");
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.show();

        StackPane arrow = (StackPane) ui.addPlotButton.lookup(".arrow");
        if (arrow != null) {
            arrow.setVisible(false);
            arrow.setManaged(false); // Ensures the space is reclaimed
        }
        ui.addPlotButton.setPadding(new Insets(5, 10, 5, 10));

        currentScene.getPlotManager().addPlot(
            new VectorFieldPlot("yes",
            (x, y) -> y,
            (x, y) -> (1 - x*x)*y - x,
            0,
            new Point(1,1), 
            Color.RED));

        for(Node i : graphStack.getChildren()){
            if(i instanceof Graph){  
                ((Graph)i).widthProperty().bind(graphStack.widthProperty());
                ((Graph)i).heightProperty().bind(graphStack.heightProperty());
            }
        }
        new AnimationTimer() {
            public void handle(long arg0){
                currentScene.update();
                currentScene.render();
            }
        }.start();
    }

    public void initializeMenus(){
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
    }
    public void initializePlotControls(){
        ui = new UIPanel(400, 900, currentScene);
        ui.setMinWidth(400);
    }

    public void initializeModeControls(){
        modeControls = new HBox();
        modeControls.setPadding(new Insets(5, 0, 5, 5));
        modeControls.setPrefHeight(40);
        graphControls.setLeft(modeControls);
        modeType = new ComboBox<>(); 
        modeType.getItems().addAll(GraphType.values());
        modeType.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(2)
            )
        ));
        modeType.setPadding(new Insets(2, 0, 2, 0));
        modeType.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(3),
                new Insets(2)
            )
        ));

        modeType.setPrefWidth(150);

        modeControls.getChildren().add(modeType);
    }

    public void initializeRenderControls(){
        renderControls = new HBox();
        renderControls.setPrefWidth(155);
        renderControls.setPadding(new Insets(0, 5, 0, 5));
        renderControls.setAlignment(Pos.CENTER_RIGHT);
        graphControls.setRight(renderControls);
        Button button = new Button("⚙");
        button.setFont(new Font(15));
        button.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(2)
            )
        ));
        button.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(3),
                new Insets(2)
            )
        ));

        Popup popup = new Popup();
        VBox options = new VBox();
        options.setMinSize(100, 300);
        options.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(2),
                new Insets(2)
            )
        ));
        options.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGREY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(2),
                new BorderWidths(2)
            )
        ));
        popup.getContent().add(options);

        CheckBox showGridLines = new CheckBox("Show Gridlines");
        showGridLines.setSelected(true);
        showGridLines.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                currentScene.getGraph().settings.setShowGridlines(showGridLines.isSelected());
            }
        });
        options.getChildren().add(showGridLines);
        CheckBox showAxesTicks = new CheckBox("Show Axes Ticks");
        showAxesTicks.setSelected(true);
        showAxesTicks.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                currentScene.getGraph().settings.setShowAxesTicks(showAxesTicks.isSelected());
            }
        });
        options.getChildren().add(showAxesTicks);
        CheckBox showTickNumbering = new CheckBox("Show Tick Numbering");
        showTickNumbering.setSelected(true);
        showTickNumbering.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                currentScene.getGraph().settings.setShowTickNumbering(showTickNumbering.isSelected());
            }
        });
        options.getChildren().add(showTickNumbering);
        button.setOnAction(new EventHandler<ActionEvent>() {
           @Override
            public void handle(ActionEvent e){
                if(!popup.isShowing()) {
                popup.show(
                    button,
                    renderControls.localToScreen(button.getBoundsInLocal()).getMaxX() - renderControls.getWidth(),
                    renderControls.localToScreen(button.getBoundsInLocal()).getMaxY()
                );
                }else{
                    popup.hide();
                }
            }});
        
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        popup.hide();
        renderControls.getChildren().add(button);
    }

    public void initializeViewControls(){
        viewControls = new HBox();
        viewControls.setAlignment(Pos.CENTER);
        viewControls.setSpacing(10);
        BorderPane.setAlignment(viewControls, Pos.CENTER);
        graphControls.setCenter(viewControls);
        viewControls.getChildren().add(new Button(){
            {
                setText(" \u2795 ");
                setFont(new Font(15));
                setPadding(new Insets(0, 0, 0, 0));
                setPrefSize(35, 35);
                setBorder(new Border(
                    new BorderStroke(
                        Color.LIGHTGRAY,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(3),
                        new BorderWidths(2)
                    )
                ));
                setBackground(new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(3),
                        new Insets(2)
                    )
                ));
                
                // 1. Pre-define a soft hover background (e.g., 8% opacity black)

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e){
                        currentScene.getCameraSystem().handle(new CameraIntent(
                            0, 0,
                            .2,
                            0, 0,
                            false,
                            graph.getInput().mouseX,
                            graph.getInput().mouseY
                        ));
                    }    
                });
            }
        });
        viewControls.getChildren().add(new Button(){
            {
                setText(" \u2796 ");
                setFont(new Font(15));
                setPadding(new Insets(0, 0, 0, 0));
                setPrefSize(35, 35);
                setBorder(new Border(
                    new BorderStroke(
                        Color.LIGHTGRAY,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(3),
                        new BorderWidths(2)
                    )
                ));
                setBackground(new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(3),
                        new Insets(2)
                    )
                ));
                
                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e){
                    currentScene.getCameraSystem().handle(new CameraIntent(
                            0, 0,
                            -.2,
                            0, 0,
                            false,
                            graph.getInput().mouseX,
                            graph.getInput().mouseY
                        ));
                    }
                });
            }
        });

        viewControls.getChildren().add(new Button(){
            {
                setText(" \u2302 ");
                setFont(new Font(15));
                setPadding(new Insets(0, 0, 0, 0));
                setPrefSize(35, 35);
                setBorder(new Border(
                    new BorderStroke(
                        Color.LIGHTGRAY,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(3),
                        new BorderWidths(2)
                    )
                ));
                setBackground(new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(3),
                        new Insets(2)
                    )
                ));
                
                // 1. Pre-define a soft hover background (e.g., 8% opacity black)

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e){
                        currentScene.getCameraSystem().resetView();
                    }
                });
            }
        });

        viewControls.getChildren().add(new Button(){
            {
                setText(" \u21F2 ");
                setFont(new Font(15));
                setPadding(new Insets(0, 0, 0, 0));
                setPrefSize(35, 35);
                setBorder(new Border(
                    new BorderStroke(
                        Color.LIGHTGRAY,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(3),
                        new BorderWidths(2)
                    )
                ));
                setBackground(new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(3),
                        new Insets(2)
                    )
                ));
                
                // 1. Pre-define a soft hover background (e.g., 8% opacity black)

                setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e){
                        currentScene.getCameraSystem().resetAspectRatio();
                    }
                });
            }
        });

        viewControls.getChildren().add(new HBox(){
            {
                setAlignment(Pos.CENTER);
                getChildren().add(new Label("Go to"){
                    {
                        setFont(new Font(12));
                        setPadding(new Insets(5, 10, 5, 0));
                    }
                });
                getChildren().add(new Label("x: "){
                    {
                        setFont(new Font(9));
                        setBorder(new Border(new BorderStroke(
                            Color.rgb(220, 220, 220),       // A soft, light gray color
                            BorderStrokeStyle.SOLID,        // Solid line style
                            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
                            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
                        )));
                        setPadding(new Insets(3, 0, 3, 5));
                    }
                });
                TextField xField = new TextField("0");
                getChildren().add(xField);
                
                getChildren().add(new Label(" y:"){
                    {
                        setFont(new Font(9));
                        setBorder(new Border(new BorderStroke(
                            Color.rgb(220, 220, 220),       // A soft, light gray color
                            BorderStrokeStyle.SOLID,        // Solid line style
                            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
                            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
                        )));
                        setPadding(new Insets(3, 0, 3, 5));
                    }
                });
                TextField yField = new TextField("0");
                getChildren().add(yField);

                xField.setBackground(new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(0, 2, 2, 0, false),
                    new Insets(2, 2, 2, 0)
                )));
                xField.setBorder(new Border(new BorderStroke(
                    Color.rgb(220, 220, 220),       // A soft, light gray color
                    BorderStrokeStyle.SOLID,        // Solid line style
                    new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
                    new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
                )));
                xField.setPadding(new Insets(3,0,3,0));
                xField.setFont(new Font(9));
                xField.setPrefWidth(30);

                yField.setBackground(new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(0, 2, 2, 0, false),
                    new Insets(2, 2, 2, 0)
                )));
                yField.setBorder(new Border(new BorderStroke(
                    Color.rgb(220, 220, 220),       // A soft, light gray color
                    BorderStrokeStyle.SOLID,        // Solid line style
                    new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
                    new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
                )));
                yField.setPadding(new Insets(3,0,3,0));
                yField.setFont(new Font(9));
                yField.setPrefWidth(30);
                
                setPadding(new Insets(0, 0, 0, 0));
                setPrefSize(170, 35);
                setMaxHeight(35);
                setBorder(new Border(
                    new BorderStroke(
                        Color.LIGHTGRAY,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(3),
                        new BorderWidths(2)
                    )
                ));
                setBackground(new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(3),
                        new Insets(2)
                    )
                ));

                setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e){
                        currentScene.getCameraSystem().goTo(Double.parseDouble(xField.getText()), Double.parseDouble(yField.getText()));
                    }
                });
            }
        });
    }
}
enum GraphType{
    CARTESIAN
}
