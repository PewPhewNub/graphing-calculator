package ui;

import app.GraphApplication;
import app.WindowManager;
import interaction.UndoManager;
import interaction.commands.AddGraphCommand;
import interaction.commands.RemoveGraphCommand;
import javafx.animation.AnimationTimer;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scene.FunctionGraphScene;
import settings.SettingsListener;
import settings.SettingsManager;
import settings.Theme;

public class MainWindow implements SettingsListener{
    private Stage stage;
    private TabPane tabPane;
    private GraphApplication app;
    private SettingsManager settingsManager;
    private WindowManager windowManager;
    private FileActions fileActions;
    private UndoManager undoManager;
    Scene scene;
    MenuBar menuBar;

    public MainWindow(Stage stage, GraphApplication app, WindowManager windowManager, SettingsManager settingsManager){
        this.app = app;
        undoManager = new UndoManager();
        tabPane = new TabPane();
        this.windowManager = windowManager;
        this.settingsManager = settingsManager;
        settingsManager.addListener(this);
        this.fileActions = new FileActions(stage, tabPane);
        this.stage = stage;
        
        initializeMenus();
        
        tabPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tabPane.setMinSize(0, 100);

        GraphTab graphTab = new GraphTab("Graph 1", new FunctionGraphScene(1200, 850));
        tabPane.getTabs().add(graphTab);
        graphTab.setUndoManager(undoManager);

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
        graphTab.setUndoManager(undoManager);
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

    @Override
    public void themeChanged(Theme theme) {
        
    }
}
enum GraphType{
    CARTESIAN
}
