package io.github.pewphewnub.ui.components;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class CloseButton extends Pane{
    public CloseButton(){
        try {
            FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/io/github/pewphewnub/ui/shell/fxml/CloseButton.fxml"
            ));

            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            getStylesheets().add(
                getClass().getResource(
                    "/io/github/pewphewnub/ui/shell/css/CloseButton.css"
                ).toExternalForm()
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to load editor actions", e
            );
        }
    }
}
