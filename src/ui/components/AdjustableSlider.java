package ui.components;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class AdjustableSlider extends BorderPane {
    public Slider slider;
    public TextField minLabel;
    public TextField maxLabel;
    public TextField textLabel;
    public TextField valueLabel;

    public double tickSize;
    
    public AdjustableSlider(String text, double min, double max, double tickSize, double initial){
        new BorderPane();
        setPadding(new Insets(5, 25, 5, 25));
        Background whiteBackground = new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(2),
                Insets.EMPTY
            )
        );

        slider = new Slider(min, max, initial);
        slider.setMajorTickUnit(tickSize);
        slider.setShowTickLabels(false);

        minLabel = new TextField(Double.toString(min));
        maxLabel = new TextField(Double.toString(max));
        valueLabel = new TextField(Double.toString(initial));
        textLabel = new TextField(text);
        Label equalsLabel = new Label(" = ");

        minLabel.setTextFormatter(createTextFormatter());
        maxLabel.setTextFormatter(createTextFormatter());
        valueLabel.setTextFormatter(createTextFormatter());

        HBox topPanel = new HBox();
        topPanel.setPadding(new Insets(5, 15, 5, 15));
        textLabel.setPadding(new Insets(5));
        equalsLabel.setPadding(new Insets(5));
        valueLabel.setPadding(new Insets(5));
        HBox.setHgrow(equalsLabel, Priority.ALWAYS);
        textLabel.setAlignment(Pos.CENTER_RIGHT);
        valueLabel.setAlignment(Pos.CENTER_LEFT);
        equalsLabel.setAlignment(Pos.CENTER);
        equalsLabel.setPrefWidth(70);
        textLabel.setBorder(Border.EMPTY);
        equalsLabel.setBorder(Border.EMPTY);
        valueLabel.setBorder(Border.EMPTY);
        textLabel.setBackground(whiteBackground);
        equalsLabel.setBackground(whiteBackground);
        valueLabel.setBackground(whiteBackground);
        topPanel.getChildren().add(textLabel);
        topPanel.getChildren().add(equalsLabel);
        topPanel.getChildren().add(valueLabel);

        HBox bottomPanel = new HBox();
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(5, 15, 5, 15));
        minLabel.setPrefWidth(50);
        maxLabel.setPrefWidth(50);
        HBox.setHgrow(slider, Priority.ALWAYS);
        minLabel.setBorder(Border.EMPTY);
        maxLabel.setBorder(Border.EMPTY);
        minLabel.setBackground(whiteBackground);
        maxLabel.setBackground(whiteBackground);
        bottomPanel.getChildren().add(minLabel);
        bottomPanel.getChildren().add(slider);
        bottomPanel.getChildren().add(maxLabel);

            
        topPanel.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(5, 5, 0, 0, false),
                    new BorderWidths(2, 2, 0, 2)
                )
            )
        );
        topPanel.setBackground(whiteBackground);
        bottomPanel.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(0, 0, 5, 5, false),
                    new BorderWidths(0, 2, 2, 2)
                )
            )
        );
        bottomPanel.setBackground(whiteBackground);

        setTop(topPanel);
        setCenter(bottomPanel);


        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            setValue(Double.parseDouble(String.format("%.3f", (Double)newValue)));
        });
    }

    public double getValue(){
        return slider.getValue();
    }
    public DoubleProperty valueProperty(){
        return slider.valueProperty();
    }
    public void setMin(double min){
        minLabel.setText(Double.toString(min));
        slider.setMin(min);
    }
    public void setMax(double max){
        maxLabel.setText(Double.toString(max));
        slider.setMax(max);
    }
    public void setValue(double value){
        valueLabel.setText(Double.toString(value));
        slider.setValue(value);
    }
    public void setText(String text){
        textLabel.setText(text);
    }
    public String getText(){
        return textLabel.getText();
    }
    private TextFormatter<String> createTextFormatter(){
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow intermediate editing states
            if (newText.isEmpty()
                    || newText.equals("-")
                    || newText.equals(".")
                    || newText.equals("-.")) {
                return change;
            }

            try {
                Double.parseDouble(newText);
                return change;
            } catch (NumberFormatException e) {
                return null; // reject the edit
            }
        });
    }
    public double getMin(){
        return slider.getMin();
    }
    public double getMax(){
        return slider.getMax();
    }
    public void setOnValueChanged(Runnable runnable){
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            runnable.run();
        });
        minLabel.setOnAction(e -> {
            slider.setMin(Double.parseDouble(minLabel.getText()));
            runnable.run();
        });
        maxLabel.setOnAction(e -> {
            slider.setMax(Double.parseDouble(maxLabel.getText()));
            runnable.run();
        });
        valueLabel.setOnAction(e -> {
            slider.setValue(Double.parseDouble(valueLabel.getText()));
            runnable.run();
        });
        textLabel.setOnAction(e -> {
            runnable.run();
        });
        minLabel.focusedProperty().addListener((obs, oldFocused, focused) -> {
            if (!focused) {
                slider.setMin(Double.parseDouble(minLabel.getText()));
                runnable.run();
            }
        });
        maxLabel.focusedProperty().addListener((obs, oldFocused, focused) -> {
            if (!focused) {
                slider.setMax(Double.parseDouble(maxLabel.getText()));
                runnable.run();
            }
        });
        valueLabel.focusedProperty().addListener((obs, oldFocused, focused) -> {
            if (!focused) {
                slider.setValue(Double.parseDouble(valueLabel.getText()));
                runnable.run();
            }
        });
        textLabel.focusedProperty().addListener((obs, oldFocused, focused) -> {
            if (!focused) {
                runnable.run();
            }
        });
    }
}
