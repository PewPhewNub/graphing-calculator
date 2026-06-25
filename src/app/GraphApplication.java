package app;

import javafx.application.Platform;
import javafx.stage.Stage;
import settings.SettingsManager;

public class GraphApplication {
    private final WindowManager windowManager;
    private final SettingsManager settingsManager;

    public GraphApplication(){
        settingsManager = new SettingsManager();
        settingsManager.load();
        windowManager = new WindowManager(this, settingsManager);
    }
    public void start(Stage stage){
        windowManager.createWindow(stage);
    }
    public void newWindow(){
        windowManager.createWindow();
    }
    public void exit(){
        Platform.exit();
    }
}
