package ui.shell;

import java.io.File;

import interaction.InputController;
import interaction.UndoManager;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import plotting.PlotManager;
import rendering.camera.Viewport;
import scene.GraphScene;
import settings.ApplicationSettings;

public class GraphTab extends Tab{
    private GraphScene scene;
    private GraphToolBar toolBar;
    private StatusBar statusBar;
    private UIPanel uiPanel;
    private File projectFile;
    private String name;
    private boolean dirty;
    private UndoManager undoManager;
    private ApplicationSettings settings;

    public GraphTab(String text, GraphScene scene){
        this.scene = scene;
        this.projectFile = null;
        this.name = text;
        setText(text);
        toolBar = new GraphToolBar(scene);
        uiPanel = new UIPanel(400, 900, scene);
        this.settings = scene.getSettings();
        BorderPane mainPane = new BorderPane();

        StackPane graphHolder = new StackPane();
        graphHolder.getChildren().add(scene.getGraph());
        graphHolder.setMinSize(0, 0);
        graphHolder.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(graphHolder, Priority.ALWAYS);
        HBox.setHgrow(graphHolder, Priority.ALWAYS);

        scene.getGraph().widthProperty().bind(graphHolder.widthProperty());
        scene.getGraph().heightProperty().bind(graphHolder.heightProperty());
        scene.getGraph().widthProperty().addListener((obs,o,n) -> scene.render());
        scene.getGraph().heightProperty().addListener((obs,o,n) -> scene.render());

        statusBar = new StatusBar();

        VBox graphPane = new VBox();
        graphPane.getChildren().add(toolBar);
        graphPane.getChildren().add(graphHolder);
        graphPane.getChildren().add(statusBar);
        graphPane.setMinSize(0, 0);
        graphPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        toolBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(toolBar, Priority.ALWAYS);
        mainPane.setLeft(uiPanel);
        mainPane.setCenter(graphPane);

        scene.getPlotManager().addListener(uiPanel);
        scene.getPlotManager().setDirtyCallback(
            () -> setDirty(true)
        );
        
        VBox.setVgrow(graphHolder, Priority.ALWAYS);

        setContent(mainPane);
    }

    public GraphTab(File file, GraphScene scene){
        this(file.getName(), scene);
        this.projectFile = file;
    }

    public GraphScene getGraphScene() {
        return scene;
    }
    public HBox getToolBar(){
        return toolBar;
    }
    public UIPanel getUiPanel() {
        return uiPanel;
    }

    public File getProjectFile() {
        return projectFile;
    }
    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }
    public boolean isDirty(){
        return dirty;
    }
    public void setDirty(boolean dirty){
        this.dirty = dirty;
        updateTitle();
    }
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
        uiPanel.setUndoManager(undoManager);
    }

    public void updateTitle(){
        if(dirty){
            setText(name + "*");
        }else{
            setText(name);
        }
    }
    public ApplicationSettings getSettings() {
        return this.settings;
    }

    public void updateStatusBar(){
        InputController input = scene.getGraph().getInput();
        Viewport viewport = scene.getGraph().viewport;
        PlotManager plotManager = scene.getPlotManager();
        statusBar.update(
            input.worldX, 
            input.worldY, 
            viewport.getZoom(),
            plotManager.getCount()
        );
    }
}
