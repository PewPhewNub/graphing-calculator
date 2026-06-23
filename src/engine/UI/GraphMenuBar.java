package engine.UI;

import app.WindowManager;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GraphMenuBar extends MenuBar{

    Menu file;

    public GraphMenuBar(TabPane tabPane, MainWindow window, WindowManager windowManager, FileActions fileActions){
        setBackground(new Background(
            new BackgroundFill(
                Color.rgb(230, 230, 230),
                CornerRadii.EMPTY,
                Insets.EMPTY
            )
        ));

        file = new Menu("File");
        MenuItem newWindow = new MenuItem("New window");
        file.getItems().add(newWindow);
        newWindow.setOnAction(e -> {
            windowManager.createWindow();
        });
        newWindow.setAccelerator(KeyCombination.keyCombination("CTRL + SHIFT + N"));
        MenuItem newGraph = new MenuItem("New");
        file.getItems().add(newGraph);
        newGraph.setOnAction(e -> {
            window.addGraphScene("New Graph");
        });
        newGraph.setAccelerator(KeyCombination.keyCombination("CTRL + N"));
        MenuItem openGraph = new MenuItem("Open");
        file.getItems().add(openGraph);
        openGraph.setOnAction(e -> {
            fileActions.open();
        });
        openGraph.setAccelerator(KeyCombination.keyCombination("CTRL + O"));
        MenuItem saveGraph = new MenuItem("Save");
        file.getItems().add(saveGraph);
        saveGraph.setOnAction(e -> {
            fileActions.save((GraphTab)(tabPane.getSelectionModel().getSelectedItem()));
        });
        saveGraph.setAccelerator(KeyCombination.keyCombination("CTRL + S"));
        MenuItem saveAsGraph = new MenuItem("Save as");
        file.getItems().add(saveAsGraph);
        saveAsGraph.setOnAction(e -> {
            fileActions.saveAs((GraphTab)(tabPane.getSelectionModel().getSelectedItem()));
        });
        saveAsGraph.setAccelerator(KeyCombination.keyCombination("CTRL + A"));
        file.getItems().add(new SeparatorMenuItem());
        file.getItems().add(new MenuItem("New Project"));
        file.getItems().add(new MenuItem("Open Project"));
        file.getItems().add(new MenuItem("Save Project"));
        file.getItems().add(new MenuItem("Save Project as"));
        file.getItems().add(new SeparatorMenuItem());
        MenuItem closeGraph = new MenuItem("Close Graph");
        file.getItems().add(closeGraph);
        closeGraph.setOnAction(e -> {
            window.removeGraphScene();
        });
        closeGraph.setAccelerator(KeyCombination.keyCombination("CTRL + W"));

        MenuItem closeWindow = new MenuItem("Close Window");
        file.getItems().add(closeWindow);
        closeWindow.setOnAction(e -> {
            window.close();
        });
        closeWindow.setAccelerator(KeyCombination.keyCombination("CTRL + SHIFT + W"));
        MenuItem exit = new MenuItem("Close All and Exit");
        file.getItems().add(exit);
        exit.setOnAction(e -> {
            windowManager.closeAll();
        });
        exit.setAccelerator(KeyCombination.keyCombination("CTRL + ESC"));

        getMenus().add(file);
        getMenus().add(new Menu("Plot"));
        getMenus().add(new Menu("View"));
    }

    
}
