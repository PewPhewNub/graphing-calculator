package app;

import java.util.ArrayList;

import engine.UI.MainWindow;
import javafx.stage.Stage;

public class WindowManager {
    ArrayList<MainWindow> windows;
    private GraphApplication app;

    public WindowManager(GraphApplication app){
        this.app = app;
        windows = new ArrayList<>();
    }

    public void createWindow(Stage stage){
        MainWindow window = new MainWindow(stage, app, this);
        windows.add(window);
        window.getStage().setOnCloseRequest(e -> {
            windows.remove(window);
        });
        window.show();
    }

    public void createWindow(){
        MainWindow window = new MainWindow(new Stage(), app, this);
        windows.add(window);
        window.getStage().setOnCloseRequest(e -> {
            windows.remove(window);
        });
        window.show();
    }

    public int getWindowCount() {
        return windows.size();
    }

    public void closeAll() {
        for(MainWindow window : new ArrayList<>(windows)) {
            window.close();
        }
    }
}
