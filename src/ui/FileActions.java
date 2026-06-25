package ui;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
            "*.jgraph")
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

    public boolean unsaved(GraphTab tab){
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION);

        alert.setTitle("Unsaved Changes");
        alert.setHeaderText(
                "This project has unsaved changes.");
        alert.setContentText(
                "Save before closing?");

        ButtonType save =
                new ButtonType("Save");

        ButtonType dontSave =
                new ButtonType("Don't Save");

        ButtonType cancel =
                new ButtonType("Cancel");

        alert.getButtonTypes().setAll(
                save,
                dontSave,
                cancel);

        Optional<ButtonType> result =
            alert.showAndWait();

        if(result.isPresent()){
            if(result.get() == save){
                save(tab);
                return true;
            }

            if(result.get() == dontSave){
                return true;
            }
            if(result.get() == cancel){
                return false;
            }
        }
        return false;
    }
}
