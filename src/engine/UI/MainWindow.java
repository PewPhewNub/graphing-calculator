package engine.UI;

import java.util.Optional;

import app.GraphApplication;
import app.WindowManager;
import engine.interaction.UndoManager;
import engine.interaction.commands.AddGraphCommand;
import engine.interaction.commands.RemoveGraphCommand;
import engine.rendering.graph.Graph;
import engine.scene.FunctionGraphScene;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    private UndoManager undoManager;
    Scene scene;
    MenuBar menuBar;

    public MainWindow(Stage stage, GraphApplication app, WindowManager windowManager){
        this.app = app;
        undoManager = new UndoManager();
        tabPane = new TabPane();
        this.windowManager = windowManager;
        this.fileActions = new FileActions(stage, tabPane);
        this.stage = stage;
        
        initializeMenus();
        
        tabPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tabPane.setMinSize(0, 100);

        Tab graphTab = new GraphTab("Graph 1", new FunctionGraphScene(1200, 850));
        tabPane.getTabs().add(graphTab);

        tabPane.getTabs().addListener((ListChangeListener<Tab>) change -> {
            if(tabPane.getTabs().isEmpty()){
                close();
            }
        });
        
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
                if(tabPane.getTabs().isEmpty()) return;
                GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
                tab.getGraphScene().update();
                tab.getGraphScene().render();
            }
        }.start();
    }

    public void initializeMenus(){
        menuBar = new GraphMenuBar(tabPane, this, windowManager, fileActions, undoManager);
    }
    public void close(){
        stage.close();
    }

    public Stage getStage() {
        return stage;
    }

    public void addGraphScene(String text){
        GraphTab graphTab = new GraphTab(text, new FunctionGraphScene(1200, 850));
        graphTab.setOnCloseRequest(e -> {
            if(!graphTab.isDirty()) return;
            fileActions.unsaved(graphTab);
        });
        undoManager.execute(new AddGraphCommand(
            tabPane.getSelectionModel().getSelectedIndex() + 1,
            graphTab, 
            tabPane));
    }
    public void removeGraphScene(){
        GraphTab GraphTab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
        undoManager.execute(new RemoveGraphCommand(
            tabPane.getSelectionModel().getSelectedIndex(), 
            GraphTab, 
            tabPane));
        if(tabPane.getTabs().isEmpty()){
            close();
        }
    }
}
enum GraphType{
    CARTESIAN
}
