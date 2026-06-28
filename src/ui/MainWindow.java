package ui;

import java.util.Timer;

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
import scene.GraphScene;
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

    private static final long FIXED_STEP = 16_666_667L; // 60 Hz
    private long previous = 0;
    private long accumulator = 0;

    public MainWindow(Stage stage, GraphApplication app, WindowManager windowManager, SettingsManager settingsManager){
        this.app = app;
        undoManager = new UndoManager();
        tabPane = new TabPane();
        this.windowManager = windowManager;
        this.settingsManager = settingsManager;
        settingsManager.addListener(this);
        this.fileActions = new FileActions(stage, tabPane,this);
        this.stage = stage;
        
        initializeMenus();
        
        tabPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tabPane.setMinSize(0, 100);

        addGraphScene("New Graph");
        undoManager.clearStacks();

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
            private static final long FIXED_STEP = 16_666_667L; // 60 Hz
            private long previous = 0;
            private long accumulator = 0;
            @Override
            public void handle(long now) {
                GraphScene current = ((GraphTab)(tabPane.getSelectionModel().getSelectedItem())).getGraphScene();
                if (previous == 0)
                    previous = now;
                long delta = now - previous;
                previous = now;
                accumulator += delta;
                current.update();
                while (accumulator >= FIXED_STEP) {
                    current.fixedUpdate();
                    accumulator -= FIXED_STEP;
                }
                current.lateUpdate();
                current.render();
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
        GraphTab graphTab = new GraphTab(text, new FunctionGraphScene(1200, 850, settingsManager.getSettings()));

        graphTab.setOnCloseRequest(e -> {
            e.consume();
            graphTab.getGraphScene().getPlotManager().close();
            removeGraphScene();
            return;
        });
        graphTab.setUndoManager(undoManager);
        undoManager.execute(new AddGraphCommand(
            tabPane.getSelectionModel().getSelectedIndex() + 1,
            graphTab, 
            tabPane));
    }
    public void removeGraphScene(){
        GraphTab graphTab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
        if(graphTab.isDirty()){
            if(!fileActions.unsaved(graphTab)) return;
        }
        undoManager.execute(new RemoveGraphCommand(
            tabPane.getSelectionModel().getSelectedIndex(), 
            graphTab, 
            tabPane));
        if(tabPane.getTabs().isEmpty()){
            close();
        }
    }
    public void addGraphScene(GraphTab graphTab){
        graphTab.setOnCloseRequest(e -> {
            e.consume();
            removeGraphScene();
            return;
        });
        graphTab.setUndoManager(undoManager);
        undoManager.execute(new AddGraphCommand(
            tabPane.getSelectionModel().getSelectedIndex() + 1,
            graphTab, 
            tabPane));
    }

    @Override
    public void themeChanged(Theme theme) {
        
    }
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
}
enum GraphType{
    CARTESIAN
}
