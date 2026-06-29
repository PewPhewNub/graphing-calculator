package ui.shell;

import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import rendering.exporting.ExportOptions;

public class ExportDialog extends Dialog<ExportOptions>{
    public ExportDialog(){
        setTitle("Export Image");
        setHeaderText("Export Settings");

        TextField widthField = new TextField("1920");
        TextField heightField = new TextField("1080");

        CheckBox transparent = new CheckBox("Transparent background");

        RadioButton file = new RadioButton("Save as file");
        RadioButton copy = new RadioButton("Copy to Clipboard");

        ToggleGroup group = new ToggleGroup();
        file.setToggleGroup(group);
        copy.setToggleGroup(group);

        file.setSelected(true);

         GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);

        layout.add(new Label("Width:"), 0, 0);
        layout.add(widthField, 1, 0);

        layout.add(new Label("Height:"), 0, 1);
        layout.add(heightField, 1, 1);

        layout.add(transparent, 0, 2, 2, 1);

        layout.add(file, 0, 3);
        layout.add(copy, 1, 3);

        getDialogPane().setContent(layout);

        getDialogPane().getButtonTypes().addAll(
            ButtonType.OK,
            ButtonType.CANCEL
        );


        setResultConverter(button -> {

            if(button == ButtonType.OK){

                return new ExportOptions(
                    Integer.parseInt(widthField.getText()),
                    Integer.parseInt(heightField.getText()),
                    transparent.isSelected(),
                    copy.isSelected()
                );

            }

            return null;
        });
    }
}
