package engine.UI;

import engine.rendering.graph.Graph;
import engine.scene.GraphScene;
import javafx.scene.canvas.Canvas;
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

    public GraphTab(String text, GraphScene scene){
        this.scene = scene;
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
        
        VBox.setVgrow(graphHolder, Priority.ALWAYS);

        setContent(mainPane);
    }

    public GraphScene getGraphScene() {
        return scene;
    }
    public HBox getToolBar(){
        return toolBar;
    }
}
