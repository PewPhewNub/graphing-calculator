package engine.UI;

import java.io.File;
import java.io.IOException;

import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistence.ProjectIO;

public class FileActions {
    private final Stage stage;
    private final TabPane tabPane;

    public FileActions(Stage stage, TabPane tabPane){
        this.stage = stage;
        this.tabPane = tabPane;
    }

    public void saveAs(GraphTab tab){
        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter(
            "JGraph Project",
            "*.jgraph")
        );
        File file = chooser.showSaveDialog(stage);
        if(file == null) return;
        tab.setProjectFile(file);
        save(tab);
    }

    public void save(GraphTab tab){
        if(tab.getProjectFile()!=null){
            try {
                ProjectIO.save(tab, tab.getProjectFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            saveAs(tab);
        }
    }

    public void open(){
        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter(
            "JGraph Project",
            "*jgraph")
        );
        File file = chooser.showOpenDialog(stage);
        if(file == null) return;

        try {
            GraphTab tab = ProjectIO.load(file);
            if(tab != null){
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
