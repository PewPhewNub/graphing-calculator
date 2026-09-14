package ui.shell;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class HowToUseWindow extends Stage {

    public HowToUseWindow() {
        setTitle("How to Use");
        setWidth(900);
        setHeight(700);

        WebView webView = new WebView();

        String url = getClass()
                .getResource("/docs/HOW_TO_USE.html")
                .toExternalForm();

        webView.getEngine().load(url);

        Scene scene = new Scene(webView);

        setScene(scene);
    }
}