package io.github.pewphewnub.app;

import java.util.ArrayList;

import io.github.pewphewnub.settings.SettingsManager;
import io.github.pewphewnub.ui.shell.MainWindow;
import javafx.stage.Stage;

public class WindowManager {
    ArrayList<MainWindow> windows;
    private GraphApplication app;
    private SettingsManager settingsManager;
    public WindowManager(GraphApplication app, SettingsManager settingsManager){
        this.app = app;
        windows = new ArrayList<>();
        this.settingsManager = settingsManager;
    }

    public void createWindow(Stage stage){
        MainWindow window = new MainWindow(stage, app, this, settingsManager);
        windows.add(window);
        window.getStage().setOnCloseRequest(e -> {
            windows.remove(window);
        });
        window.show();
    }

    public void createWindow(){
        MainWindow window = new MainWindow(new Stage(), app, this, settingsManager);
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
