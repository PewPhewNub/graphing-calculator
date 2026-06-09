package Simulation.UI;

import Simulation.Graphing.Graph;
import javafx.scene.layout.VBox;

public class UIPanel extends VBox {
    Graph graph;

    public UIPanel(double width, double height, Graph graph){
        super();
        setHeight(height);
        setWidth(width);
        this.graph = graph;
    }
    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
