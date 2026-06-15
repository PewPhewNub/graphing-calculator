package engine.UI;

import engine.UI.plotEditor.ParametricPlotEditor;
import engine.rendering.Graph;
import javafx.scene.layout.VBox;
public class UIPanel extends VBox {
    Graph graph;
    public UIPanel(double width, double height, Graph graph){
        super();
        setHeight(height);
        setWidth(width);
        this.graph = graph;

        getChildren().add(new ParametricPlotEditor(graph.plotManager));
    }
    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
