package io.github.pewphewnub.ui.shell;

import java.io.IOException;

import io.github.pewphewnub.rendering.exporting.ExportOptions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ExportDialog extends Dialog<ExportOptions>{
    @FXML private RadioButton clipboardRadioButton;
    @FXML private RadioButton fileRadioButton;
    @FXML private CheckBox transparentBackgroundCheckBox;
    @FXML private TextField widthField;
    @FXML private TextField heightField;

    public ExportDialog(){
        setTitle("Export Image");
        setHeaderText("Export Settings");
        try {
            FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/io/github/pewphewnub/ui/shell/fxml/ExportDialog.fxml"
            ));

            loader.setController(this);
            
            VBox content = loader.load();

            getDialogPane().setContent(content);

            getDialogPane().getStylesheets().add(
                getClass().getResource(
                    "/io/github/pewphewnub/ui/shell/css/ExportDialog.css"
                ).toExternalForm()
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to load Export dialog", e
            );
        }

        getDialogPane().getButtonTypes().addAll(
            ButtonType.OK,
            ButtonType.CANCEL
        );

        setResultConverter(button -> {

            if(button == ButtonType.OK){

                return new ExportOptions(
                    Integer.parseInt(widthField.getText()),
                    Integer.parseInt(heightField.getText()),
                    transparentBackgroundCheckBox.isSelected(),
                    clipboardRadioButton.isSelected()
                );

            }

            return null;
        });
    }
}
