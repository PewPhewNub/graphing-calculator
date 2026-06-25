package app;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainWindow;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GraphApplication app = new GraphApplication();
        app.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}