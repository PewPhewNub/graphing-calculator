package engine.UI;

import app.GraphApplication;
import app.WindowManager;
import engine.rendering.graph.Graph;
import engine.scene.FunctionGraphScene;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindow {
    private Stage stage;
    private TabPane tabPane;
    private GraphApplication app;
    private WindowManager windowManager;
    private FileActions fileActions;
    Scene scene;
    MenuBar menuBar;

    public MainWindow(Stage stage, GraphApplication app, WindowManager windowManager){
        this.app = app;
        tabPane = new TabPane();
        this.windowManager = windowManager;
        this.fileActions = new FileActions(stage, tabPane);
        this.stage = stage;
        
        initializeMenus();
        
        tabPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tabPane.setMinSize(0, 100);

        Tab graphTab = new GraphTab("Graph 1", new FunctionGraphScene(1200, 850));
        tabPane.getTabs().add(graphTab);
        
        BorderPane pane = new BorderPane();

        pane.setTop(menuBar);
        pane.setCenter(tabPane);

        scene = new Scene(pane, 1600, 900);
    }

    public void show(){
        stage.setScene(scene);
        stage.setTitle("Almost Desmos");
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.show();

        new AnimationTimer() {
            public void handle(long arg0){
                GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
                tab.getGraphScene().update();
                tab.getGraphScene().render();
            }
        }.start();
    }

    public void initializeMenus(){
        menuBar = new GraphMenuBar(tabPane, this, windowManager, fileActions);
    }
    public void close(){
        stage.close();
    }

    public Stage getStage() {
        return stage;
    }

    public void addGraphScene(String text){
        Tab graphTab = new GraphTab(text, new FunctionGraphScene(1200, 850));
        tabPane.getTabs().add(graphTab);
    }
    public void removeGraphScene(){
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
    }
}
enum GraphType{
    CARTESIAN
}
