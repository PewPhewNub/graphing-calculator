package engine.UI;

import java.io.File;

import engine.scene.GraphScene;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GraphTab extends Tab{
    private GraphScene scene;
    private GraphToolBar toolBar;
    private UIPanel uiPanel;
    private File projectFile;
    private String name;
    private boolean dirty;

    public GraphTab(String text, GraphScene scene){
        this.scene = scene;
        this.projectFile = null;
        this.name = text;
        setText(text);
        toolBar = new GraphToolBar(scene);
        uiPanel = new UIPanel(400, 900, scene);
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

        VBox graphPane = new VBox();
        graphPane.getChildren().add(toolBar);
        graphPane.getChildren().add(graphHolder);
        graphPane.setMinSize(0, 0);
        graphPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        toolBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(toolBar, Priority.ALWAYS);
        mainPane.setLeft(uiPanel);
        mainPane.setCenter(graphPane);

        scene.getPlotManager().addListener(uiPanel);
        
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
    public void isDirty(boolean dirty){
        this.dirty = dirty;
    }
}
