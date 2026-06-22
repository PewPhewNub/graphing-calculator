package app;

import javafx.application.Platform;
import javafx.stage.Stage;

public class GraphApplication {
    private final WindowManager windowManager;

    public GraphApplication(){
        windowManager = new WindowManager(this);
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
