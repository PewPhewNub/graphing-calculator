package engine.UI;

import core.parser.Lexer;
import core.parser.Parser;
import core.parser.node.DefinitionNode;
import engine.plotting.FunctionPlot;
import engine.plotting.Plot;
import engine.rendering.Graph;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class UIPanel extends VBox {
    Graph graph;
    String lastValidName;
    public UIPanel(double width, double height, Graph graph){
        super();
        setHeight(height);
        setWidth(width);
        this.graph = graph;

        getChildren().add(new InputFunctionPanel(graph));
    }
    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
