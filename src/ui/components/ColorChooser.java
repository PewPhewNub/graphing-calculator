package ui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

public class ColorChooser extends StackPane{
    private ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.RED);
    
    private StackPane colorDisplay;
    
    private Popup popup;
    private VBox popupContent;

    private FlowPane swatchPane;
    private Button advancedButton;

    private VBox advancedPane;
    private boolean isAdvancedShow = false;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private Label redLabel;
    private Label greenLabel;
    private Label blueLabel;

    private TextField hexField;

    private static final Color[] DEFAULT_COLORS = {
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.PURPLE,
        Color.ORANGE,
        Color.LIMEGREEN,
        Color.PINK,
        Color.DARKGRAY,
        Color.BLACK
    };

    public Color getSelectedColor() {
        return colorProperty.getValue();
    }

    public void setSelectedColor(Color color) {
        colorProperty.setValue(color);
    }

    public ObjectProperty<Color> colorProperty(){
        return colorProperty;
    }
    
    public ColorChooser(Color color){
        setVisible(true);
        setSelectedColor(color);

        colorDisplay = new StackPane();
        colorDisplay.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(12.5), 
                new BorderWidths(2)
            )
        ));

        colorDisplay.setBackground(
                new Background(
                    new BackgroundFill(
                        color,
                        new CornerRadii(15),
                        new Insets(3)
                    )
                )
            );

        colorProperty.addListener((obs, oldColor, newColor) -> {
            if(!hexField.isFocused()){
                hexField.setText(toHex(newColor));
            }
            if(!redSlider.isFocused()){
                redSlider.setValue(newColor.getRed()*255);
            }
            if(!greenSlider.isFocused()){
                greenSlider.setValue(newColor.getGreen()*255);
            }
            if(!blueSlider.isFocused()){
                blueSlider.setValue(newColor.getBlue()*255);
            }
            colorDisplay.setBackground(
                new Background(
                    new BackgroundFill(
                        newColor,
                        new CornerRadii(15),
                        new Insets(3)
                    )
                )
            );
        });

        colorDisplay.setMinSize(30, 30);

        popup = new Popup();
        
        popupContent = new VBox();
        popupContent.setBackground(new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(5),
                        new Insets(2)
                    )
                ));
        popupContent.getChildren().add(new Label("Default Colors"){
            {
                new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(5),
                        new Insets(2)
                    )
                );
                setPadding(new Insets(5, 25, 5, 15));
                setManaged(true);
            }
        });
        popupContent.setOpacity(1);
        popupContent.setPrefWidth(300);

        popupContent.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(5), 
                new BorderWidths(2)
            )
        ));

        swatchPane = new FlowPane();
        swatchPane.setPadding(new Insets(5, 0, 5, 15));

        for(Color i : DEFAULT_COLORS){
            Circle circle = new Circle(10);
            circle.setFill(i);

            circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e){
                    setSelectedColor(i);
                }
            });

            swatchPane.getChildren().add(circle);
            swatchPane.getChildren().add(new Separator(){
                {
                    setBorder(Border.EMPTY);
                    setVisible(false);
                    setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
                    setPadding(new Insets(2));
                    setOrientation(Orientation.VERTICAL);
                }
            });
        }

        advancedButton = new Button();
        advancedButton.setBorder(
            Border.EMPTY
        );

        advancedButton.setBackground(
                new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(0),
                        new Insets(0)
                    )
                )
            );
        advancedButton.setPadding(new Insets(5, 25, 5, 5));
        advancedButton.setFont(new Font(10));
        advancedButton.setText('\u25BE' + " Show " + "advanced options");
        advancedButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                isAdvancedShow = !isAdvancedShow;
                advancedButton.setText(((isAdvancedShow)?'\u25B6' + " Hide ":'\u25BE' + " Show ") + "advanced options");
                advancedPane.setVisible(isAdvancedShow);
                advancedPane.setManaged(isAdvancedShow);
            }
        });

        advancedPane = new VBox();
        hexField = new TextField("FF0000");
        advancedPane.getChildren().add(new HBox(){
            {
                getChildren().add(new Label(("HEX:")){
                    {
                        setPadding(new Insets(4.5, 10, 1.5, 25));
                    }
                });

                getChildren().add(new Label(("#")){
                    {
                        setAlignment(Pos.CENTER_LEFT);
                        setPadding(new Insets(3, 0, 3, 5));
                        setBorder(new Border(
                            new BorderStroke(
                                Color.LIGHTGRAY, 
                                BorderStrokeStyle.SOLID, 
                                new CornerRadii(2, 0, 0, 2, false), 
                                new BorderWidths(2, 0, 2, 2)
                            )
                        ));
                    }
                });

                getChildren().add(hexField);
            }
        });
        hexField.setBorder(Border.EMPTY);
        hexField.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(0),
                    new Insets(0)
                )
            )
        );
        hexField.setPadding(new Insets(3, 0, 3, 0));
        hexField.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(0, 2, 2, 0, false), 
                new BorderWidths(2, 2, 2, 0)
            )
        ));
        hexField.setTextFormatter(
            new TextFormatter<>(change -> {
                String text = change.getControlNewText();
                if (!text.matches("[0-9A-Fa-f]{0,6}")) {
                    return null;
                }
                change.setText(change.getText().toUpperCase());
                return change;
            })
        );
        hexField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e){
                if(hexField.getText().length() != 6) return;
                setSelectedColor(Color.web(hexField.getText()));
            }
        });

        advancedPane.setVisible(false);
        advancedPane.setManaged(false);
        
        redSlider = new Slider(0, 255, 255);
        blueSlider = new Slider(0, 255, 0);
        greenSlider = new Slider(0, 255, 0);
        redSlider.setPadding(new Insets(3, 25, 3, 25));
        greenSlider.setPadding(new Insets(3, 25, 3, 25));
        blueSlider.setPadding(new Insets(3, 25, 3, 25));

        redSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            setSelectedColor(new Color((double)newValue/255, getSelectedColor().getGreen(), getSelectedColor().getBlue(), 1));
            redLabel.setText("R: " + (int)(Math.round((double)newValue)));
        });
        greenSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            setSelectedColor(new Color(getSelectedColor().getRed(), (double)newValue/255, getSelectedColor().getBlue(), 1));
            greenLabel.setText("G: " + (int)(Math.round((double)newValue)));
        });
        blueSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            setSelectedColor(new Color(getSelectedColor().getRed(), getSelectedColor().getGreen(), (double)newValue/255, 1));
            blueLabel.setText("B: " + (int)(Math.round((double)newValue)));
        });

        advancedPane.getChildren().add(new BorderPane(){
            {
                redLabel = new Label();
                redLabel.setLabelFor(redSlider);
                redLabel.setTextFill(Color.RED);
                redLabel.setMaxWidth(75);
                redLabel.setMinWidth(75);
                redLabel.setTextAlignment(TextAlignment.LEFT);
                redLabel.setPadding(new Insets(5, 5, 5, 25));
                redSlider.setPadding(new Insets(5, 25, 5, 5));
                setLeft(redLabel);
                setCenter(redSlider);
            }
        });
        
        advancedPane.getChildren().add(new BorderPane(){
            {
                greenLabel = new Label();
                greenLabel.setLabelFor(greenSlider);
                greenLabel.setTextFill(Color.GREEN);
                greenLabel.setMaxWidth(75);
                greenLabel.setMinWidth(75);
                greenLabel.setTextAlignment(TextAlignment.LEFT);
                greenLabel.setPadding(new Insets(5, 5, 5, 25));
                greenSlider.setPadding(new Insets(5, 25, 5, 5));
                setLeft(greenLabel);
                setCenter(greenSlider);
            }
        });
        
        advancedPane.getChildren().add(new BorderPane(){
            {
                blueLabel = new Label();
                blueLabel.setLabelFor(blueSlider);
                blueLabel.setTextFill(Color.BLUE);
                blueLabel.setMaxWidth(75);
                blueLabel.setMinWidth(75);
                blueLabel.setTextAlignment(TextAlignment.LEFT);
                blueLabel.setPadding(new Insets(5, 5, 5, 25));
                blueSlider.setPadding(new Insets(5, 25, 5, 5));
                setLeft(blueLabel);
                setCenter(blueSlider);
            }
        });

        popupContent.getChildren().add(swatchPane);
        swatchPane.setManaged(true);
        popupContent.getChildren().add(advancedButton);
        popupContent.getChildren().add(advancedPane);

        popup.getContent().add(popupContent);

        getChildren().add(colorDisplay);
        colorDisplay.setOnMouseClicked(e -> {
            if (!popup.isShowing()) {
                popup.show(
                    colorDisplay,
                    localToScreen(colorDisplay.getBoundsInLocal()).getMaxX(),
                    localToScreen(colorDisplay.getBoundsInLocal()).getMinY()
                );
            }else{
                popup.hide();
            }
        });

        popup.setOpacity(1);
        popup.setAutoHide(true);
    }
    private String toHex(Color color) {

        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);

        return String.format("%02X%02X%02X", r, g, b);
    }
}
