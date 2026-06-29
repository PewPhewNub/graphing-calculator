package ui.shell;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistence.ProjectIO;
import rendering.exporting.ExportOptions;
import rendering.exporting.ImageExporter;

public class FileActions {
    private final Stage stage;
    private final TabPane tabPane;
    private final MainWindow mainWindow;

    public FileActions(Stage stage, TabPane tabPane, MainWindow mainWindow){
        this.stage = stage;
        this.tabPane = tabPane;
        this.mainWindow = mainWindow;
    }

    public void exportImage(GraphTab tab){
        ExportDialog dialog = new ExportDialog();
        ExportOptions options = dialog.showAndWait().orElse(null);
        if(options == null) return;

        export(tab, options);
    }

    public void export(GraphTab tab, ExportOptions options){
        if(!options.clipboard()){
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter png = 
            new FileChooser.ExtensionFilter(
                "PNG Image (*.png)",
                 "*.png"
                );
            chooser.getExtensionFilters().add(png);
            File file = chooser.showSaveDialog(stage);
            FileChooser.ExtensionFilter selected = chooser.getSelectedExtensionFilter();
            if (selected == png && !file.getName().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            if(file == null) return;
            
            try {
                ImageExporter.exportPNG(tab, file, options.width(), options.height(), options.transparent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            ImageExporter.copyImage(tab, options);
        }
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
        tab.setDirty(false);
        save(tab);
    }

    public void save(GraphTab tab){
        if(tab.getProjectFile()!=null){
            try {
                ProjectIO.save(tab, tab.getProjectFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            tab.setDirty(false);
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
                mainWindow.addGraphScene(tab);
                tabPane.getSelectionModel().select(tab);
                tab.setDirty(false);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Open Project");
            alert.setHeaderText("Invalid project file");
            alert.setContentText(
                "The selected file is not a valid project."
            );
            alert.showAndWait();
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
                new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

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
        }else if(result.isEmpty()){
            return false;
        }
        return false;
    }
}
