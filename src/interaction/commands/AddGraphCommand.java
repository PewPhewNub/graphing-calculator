package interaction.commands;

import javafx.scene.control.TabPane;
import ui.GraphTab;

public class AddGraphCommand implements Command{
    GraphTab graph;
    int index;
    TabPane tabPane;
    public AddGraphCommand(int index, GraphTab graph, TabPane tabPane){
        this.graph = graph;
        this.tabPane = tabPane;
        this.index = index;
    }

    @Override
    public void execute() {
        tabPane.getTabs().add(index, graph);
        tabPane.getSelectionModel().select(graph);
    }

    @Override
    public void undo() {
        tabPane.getTabs().remove(graph);
    }
}
