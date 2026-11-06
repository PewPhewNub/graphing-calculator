
package io.github.pewphewnub.ui.shell;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

public class About extends Alert {

    public About() {
        super(AlertType.INFORMATION);

        setTitle("About");
        setHeaderText(null);
        setContentText(null);

        try {
            FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/io/github/pewphewnub/ui/shell/fxml/About.fxml"
            )
        );

            VBox content = loader.load();

            getDialogPane().setContent(content);

            getDialogPane().getStylesheets().add(
                getClass().getResource(
                    "/io/github/pewphewnub/ui/shell/css/About.css"
                ).toExternalForm()
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to load About dialog", e
            );
        }
    }
}
