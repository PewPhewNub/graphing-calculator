package ui;

import app.WindowManager;
import interaction.UndoManager;
import javafx.geometry.Insets;
import javafx.scene.control.CheckMenuItem;
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
import rendering.camera.CameraIntent;

public class GraphMenuBar extends MenuBar{

    Menu file;
    Menu view;
    Menu edit;

    public GraphMenuBar(TabPane tabPane, MainWindow window, WindowManager windowManager, FileActions fileActions, UndoManager undoManager){
        setBackground(new Background(
            new BackgroundFill(
                Color.rgb(230, 230, 230),
                CornerRadii.EMPTY,
                Insets.EMPTY
            )
        ));
        populateFileMenu(tabPane, window, windowManager, fileActions);
        getMenus().add(new Menu("Plot"));
        populateViewMenu(tabPane);
        populateEditMenu(undoManager);
    }
    public void populateFileMenu(TabPane tabPane, MainWindow window, WindowManager windowManager, FileActions fileActions){
        file = new Menu("File");
        MenuItem newGraph = new MenuItem("New");
        file.getItems().add(newGraph);
        newGraph.setOnAction(e -> {
            window.addGraphScene("New Graph");
        });
        newGraph.setAccelerator(KeyCombination.keyCombination("CTRL + N"));
        MenuItem newWindow = new MenuItem("New window");
        file.getItems().add(newWindow);
        newWindow.setOnAction(e -> {
            windowManager.createWindow();
        });
        newWindow.setAccelerator(KeyCombination.keyCombination("CTRL + SHIFT + N"));
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

        getMenus().add(file);
    }

    public void populateViewMenu(TabPane tabPane){
        view = new Menu("View");

        CheckMenuItem showGrid = new CheckMenuItem("Show Grid");
        showGrid.setSelected(true);
        view.getItems().add(showGrid);
        showGrid.setOnAction(e -> {
            GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
            tab.getGraphScene().getSettings().showGrid = showGrid.isSelected();
        });
        CheckMenuItem showAxes = new CheckMenuItem("Show Axes");
        view.getItems().add(showAxes);
        showAxes.setSelected(true);
        CheckMenuItem showTickMarks = new CheckMenuItem("Show Grid");
        showTickMarks.setSelected(true);
        view.getItems().add(showTickMarks);
        showTickMarks.setOnAction(e -> {
            GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
            tab.getGraphScene().getSettings().showTickMarks = showTickMarks.isSelected();
        });
        showAxes.setOnAction(e -> {
            GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
            tab.getGraphScene().getSettings().showAxes = showAxes.isSelected();

            if(!showAxes.isSelected()){
                showTickMarks.setDisable(true);
                tab.getGraphScene().getSettings().showTickMarks = false;
            }else{
                showTickMarks.setDisable(false);
                tab.getGraphScene().getSettings().showTickMarks = showTickMarks.isSelected();
            }
        });

        view.getItems().add(new SeparatorMenuItem());

        MenuItem zoomIn = new MenuItem("Zoom in");
        view.getItems().add(zoomIn);
        zoomIn.setOnAction(e -> {
            GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
            tab.getGraphScene().getCameraSystem().handle(
                new CameraIntent(0, 0, 0.2, 0, 0, false, Double.NaN, Double.NaN)
            );
        });
        zoomIn.setAccelerator(KeyCombination.keyCombination("CTRL + EQUALS"));
        MenuItem zoomOut = new MenuItem("Zoom in");
        view.getItems().add(zoomOut);
        zoomOut.setOnAction(e -> {
            GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
            tab.getGraphScene().getCameraSystem().handle(
                new CameraIntent(0, 0, -0.2, 0, 0, false, Double.NaN, Double.NaN)
            );
        });
        zoomOut.setAccelerator(KeyCombination.keyCombination("CTRL + MINUS"));
        MenuItem resetView = new MenuItem("Reset View");
        view.getItems().add(resetView);
        resetView.setOnAction(e -> {
            GraphTab tab = (GraphTab)tabPane.getSelectionModel().getSelectedItem();
            tab.getGraphScene().getCameraSystem().resetView();
        });
        resetView.setAccelerator(KeyCombination.keyCombination("CTRL + SHIFT + EQUALS"));
        
        getMenus().add(view);
    }

    public void populateEditMenu(UndoManager undoManager){
        edit = new Menu("Edit");
        MenuItem undo = new MenuItem("Undo");
        edit.getItems().add(undo);
        undo.setOnAction(e -> {
            if(undoManager.canUndo())undoManager.undo();
        });
        undo.setAccelerator(KeyCombination.keyCombination("CTRL + Z"));
        
        MenuItem redo = new MenuItem("Redo");
        edit.getItems().add(redo);
        redo.setOnAction(e -> {
            if(undoManager.canRedo())undoManager.redo();
        });
        redo.setAccelerator(KeyCombination.keyCombination("CTRL + Y"));

        getMenus().add(edit);
    }
}
