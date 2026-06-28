package interaction.commands;

import javafx.scene.control.TabPane;
import ui.shell.GraphTab;

public class RemoveGraphCommand implements Command{
    GraphTab graph;
    int index;
    TabPane tabPane;
    public RemoveGraphCommand(int index, GraphTab graph, TabPane tabPane){
        this.graph = graph;
        this.tabPane = tabPane;
        this.index = index;
    }

    @Override
    public void undo() {
        tabPane.getTabs().add(index, graph);
        tabPane.getSelectionModel().select(graph);
    }

    @Override
    public void execute() {
        tabPane.getTabs().remove(graph);
    }
}
