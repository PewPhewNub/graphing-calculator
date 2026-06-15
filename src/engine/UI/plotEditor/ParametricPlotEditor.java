package engine.UI.plotEditor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import core.parser.Lexer;
import core.parser.Parser;
import core.parser.node.DefinitionNode;
import engine.UI.ColorChooser;
import engine.plotting.PlotManager;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ParametricPlot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class ParametricPlotEditor extends PlotEditor{

    public ColorChooser colorChooser;
    public BorderPane topPanel;

    public String inputFunction;
    public HBox functionInputPanel;
    public TextField functionInputField;
    public Label functionInputLabel;

    public String inputFunction1;
    public HBox functionInputPanel1;
    public TextField functionInputField1;
    public Label functionInputLabel1;

    public String dependent1 = "x";
    public String dependent2 = "y";
    public String independent = "t";

    private Label independentVarLabel;
    private TextField independentVarField;
    private TextField minimum; private TextField maximum;
    private TextField maxSamples;

    private Button advancedButton;
    private boolean isAdvancedShow = false;
    private VBox advancedOptionsPanel;

    public ParametricPlotEditor(PlotManager plotManager){
        this.plotManager = plotManager;

        setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(5),
                new Insets(2)
            )
        ));

        setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGREY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(2)
            )
        ));
        
        colorChooser = new ColorChooser(Color.RED);
        colorChooser.setAlignment(Pos.CENTER_RIGHT);
        colorChooser.colorProperty().addListener((obs, oldColor, newColor) -> {
            try {
                buildPlot();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });

        functionInputPanel = new HBox();
        functionInputLabel = new Label("x(t) = ");
        functionInputLabel.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
        )));
        functionInputLabel.setFont(new Font(20));
        functionInputLabel.setTextAlignment(TextAlignment.LEFT);
        functionInputLabel.setPadding(new Insets(5,0,5,15));

        functionInputField = new TextField("t^2");
        functionInputField.setFont(new Font(20));
        functionInputField.setAlignment(Pos.CENTER_LEFT);
        functionInputField.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                try {
                    buildPlot();
                } catch (Exception e1) {
                    System.out.println(e1.getMessage());
                }
            }
        );
        functionInputField.setTextFormatter(new TextFormatter<>(change ->{
            String text = change.getText();
            if(text.equals("(")) text = "()";
            change.setText(text);
            return change;
        }));
        functionInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains("theta")) {
                // Use Platform.runLater to avoid conflicts with the ongoing text update
                Platform.runLater(() -> {
                    int caretPosition = functionInputField.getCaretPosition();
                    
                    // Replace the text
                    String replaced = newValue.replace("theta", "\u03B8");
                    functionInputField.setText(replaced);
                    
                    // Adjust caret position so it doesn't jump to the beginning
                    functionInputField.positionCaret(caretPosition - 4); 
                });
            }
        });

        functionInputField.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        functionInputField.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
            new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
        )));
        functionInputField.setPadding(new Insets(5,0,5,0));
        
        functionInputPanel.getChildren().add(functionInputLabel);
        functionInputPanel.getChildren().add(functionInputField);
        functionInputPanel.setPadding(new Insets(5, 25, 5, 25));

        functionInputPanel1 = new HBox();
        functionInputLabel1 = new Label("y(t) = ");
        functionInputLabel1.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
        )));
        functionInputLabel1.setFont(new Font(20));
        functionInputLabel1.setTextAlignment(TextAlignment.LEFT);
        functionInputLabel1.setPadding(new Insets(5,0,5,15));

        functionInputField1 = new TextField("2t");
        functionInputField1.setFont(new Font(20));
        functionInputField1.setAlignment(Pos.CENTER_LEFT);
        functionInputField1.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                try {
                    buildPlot();
                } catch (Exception e1) {
                    System.out.println(e1.getMessage());
                }
            }
        );
        functionInputField1.setTextFormatter(new TextFormatter<>(change ->{
            String text = change.getText();
            if(text.equals("(")) text = "()";
            change.setText(text);
            return change;
        }));
        functionInputField1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains("theta")) {
                // Use Platform.runLater to avoid conflicts with the ongoing text update
                Platform.runLater(() -> {
                    int caretPosition = functionInputField1.getCaretPosition();
                    
                    // Replace the text
                    String replaced = newValue.replace("theta", "\u03B8");
                    functionInputField1.setText(replaced);
                    
                    // Adjust caret position so it doesn't jump to the beginning
                    functionInputField1.positionCaret(caretPosition - 4); 
                });
            }
        });

        functionInputField1.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        functionInputField1.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
            new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
        )));
        functionInputField1.setPadding(new Insets(5,0,5,0));
        
        functionInputPanel1.getChildren().add(functionInputLabel1);
        functionInputPanel1.getChildren().add(functionInputField1);
        functionInputPanel1.setPadding(new Insets(5, 25, 5, 25));

        advancedButton = new Button();
        advancedButton.setBorder(
            Border.EMPTY
        );

        advancedButton.setBackground(
                new Background(
                    new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(0, 0, 0, 15, false),
                        new Insets(0)
                    )
                )
            );
        advancedButton.setPadding(new Insets(5, 25, 5, 5));
        advancedButton.setFont(new Font(10));
        advancedButton.setText('\u25BE' + " Show " + "more options");
        advancedButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                isAdvancedShow = !isAdvancedShow;
                advancedButton.setText(((isAdvancedShow)?'\u25B6' + " Hide ":'\u25BE' + " Show ") + "more options");
                advancedOptionsPanel.setVisible(isAdvancedShow);
                advancedOptionsPanel.setManaged(isAdvancedShow);
            }
        });

        advancedOptionsPanel =new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        HBox box1 = new HBox();
        box1.setPadding(new Insets(5, 25, 5, 25));

        independentVarLabel = new Label("Independent Variable :");
        independentVarLabel.setPadding(new Insets(5, 5, 5, 10));
        independentVarLabel.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(2, 0, 0, 2, false),
            new Insets(2, 0, 2, 2)
        )));
        
        independentVarLabel.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
        )));
       
        box1.getChildren().add(independentVarLabel);
        independentVarField = new TextField();
        independentVarField.setText("t");

        independentVarField.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        independentVarField.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
            new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
        )));
        independentVarField.setPadding(new Insets(5, 0, 5, 0));

        independentVarField.textProperty().addListener((obs, oldValue, newValue) -> {
            independent = independentVarField.getText();
            functionInputLabel.setText(dependent1 + "(" + independent + ") = ");
            functionInputLabel1.setText(dependent2 + "(" + independent + ") = ");
            try {
                buildPlot();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });

        box1.getChildren().add(independentVarField);

        this.getChildren().add(functionInputPanel);
        this.getChildren().add(functionInputPanel1);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box1);

        topPanel = new BorderPane();
        getChildren().add(0, topPanel);
        topPanel.setLeft(colorChooser);
        topPanel.setCenter(new Label("Function Plot"){
            {
                setAlignment(Pos.CENTER);
            }
        });

        Pane icon = new Pane();

        Line l1 = new Line(8, 8, 22, 22);
        Line l2 = new Line(22, 8, 8, 22);

        l1.setStrokeWidth(2.5);
        l2.setStrokeWidth(2.5);
        l1.setStroke(Color.GRAY);
        l2.setStroke(Color.GRAY);

        icon.getChildren().addAll(l1, l2);

        icon.setPadding(new Insets(5, 5, 0, 0));
        
        topPanel.setRight(icon);
        icon.setOnMouseClicked(e ->{
            close();
        });

        HBox minBox = new HBox();
        minBox.getChildren().add(new Label(){
            {
                setText("Min : ");
                setBackground(new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(0, 2, 2, 0, false),
                    new Insets(2, 2, 2, 0)
                )));
                
                setBorder(new Border(new BorderStroke(
                    Color.rgb(220, 220, 220),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2, 0, 0, 2, false),
                    new BorderWidths(2, 0, 2, 2)
                )));
                setPadding(new Insets(5, 0, 5, 10));
            }
        });
        minimum = new TextField();
        minimum.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        minimum.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),
            BorderStrokeStyle.SOLID,
            new CornerRadii(0, 2, 2, 0, false),
            new BorderWidths(2, 2, 2, 0)
        )));
        minimum.setPadding(new Insets(5, 0, 5, 0));

        minimum.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                buildPlot();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });
        minimum.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains("theta")) {
                // Use Platform.runLater to avoid conflicts with the ongoing text update
                Platform.runLater(() -> {
                    int caretPosition = minimum.getCaretPosition();
                    
                    // Replace the text
                    String replaced = newValue.replace("theta", "\u03B8");
                    minimum.setText(replaced);
                    
                    // Adjust caret position so it doesn't jump to the beginning
                    minimum.positionCaret(caretPosition - 4); 
                });
            }
        });
        
        minBox.getChildren().add(minimum);
        
        HBox maxBox = new HBox();
        maxBox.getChildren().add(new Label(){
            {
                setText("Max : ");
                setBackground(new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(0, 2, 2, 0, false),
                    new Insets(2, 2, 2, 0)
                )));
                
                setBorder(new Border(new BorderStroke(
                    Color.rgb(220, 220, 220),       // A soft, light gray color
                    BorderStrokeStyle.SOLID,        // Solid line style
                    new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
                    new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
                )));
                setPadding(new Insets(5, 0, 5, 10));
            }
        });
        maximum = new TextField("50");
        maximum.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        maximum.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
            new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
        )));
        maximum.setPadding(new Insets(5, 0, 5, 0));

        maximum.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                buildPlot();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });
        
        maximum.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains("theta")) {
                // Use Platform.runLater to avoid conflicts with the ongoing text update
                Platform.runLater(() -> {
                    int caretPosition = maximum.getCaretPosition();
                    
                    // Replace the text
                    String replaced = newValue.replace("theta", "\u03B8");
                    maximum.setText(replaced);
                    
                    // Adjust caret position so it doesn't jump to the beginning
                    maximum.positionCaret(caretPosition - 4); 
                });
            }
        });
        maxBox.getChildren().add(maximum);

        minimum.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.matches("-?\\d*(\\.\\d*)?")) {
                return change;
            }

            return null;
        }));
        maximum.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.matches("-?\\d*(\\.\\d*)?")) {
                return change;
            }

            return null;
        }));

        minBox.setPadding(new Insets(5, 25, 5, 25));
        maxBox.setPadding(new Insets(5, 25, 5, 25));

        minimum.setText("0");
        maximum.setText("50");

        advancedOptionsPanel.getChildren().add(minBox);
        advancedOptionsPanel.getChildren().add(maxBox);

        HBox sampleBox = new HBox();
        sampleBox.getChildren().add(new Label(){
            {
                setText("Max samples : ");
                setBackground(new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(0, 2, 2, 0, false),
                    new Insets(2, 2, 2, 0)
                )));
                
                setBorder(new Border(new BorderStroke(
                    Color.rgb(220, 220, 220),       // A soft, light gray color
                    BorderStrokeStyle.SOLID,        // Solid line style
                    new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
                    new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
                )));
                setPadding(new Insets(5, 0, 5, 10));
            }
        });
        maxSamples = new TextField("50000");
        maxSamples.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        maxSamples.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
            new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
        )));
        maxSamples.setPadding(new Insets(5, 0, 5, 0));

        maxSamples.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                buildPlot();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });

        maxSamples.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        sampleBox.getChildren().add(maxSamples);
        sampleBox.setPadding(new Insets(5, 0, 5, 25));

        try {
            buildPlot();
        } catch(Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    @Override
    public void buildPlot() throws Exception {
        String text = functionInputField.getText();
        String text2 = functionInputField1.getText();
        
        Lexer lexer = new Lexer(text);
        Lexer lexer2 = new Lexer(text2);
        Map<String, Double> map = new HashMap<>();
        try {
            lexer.tokenize();
            lexer2.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = parser.parseDefinition(
                        dependent1,
                        Set.of(independent)
                    );
            Parser parser2 = new Parser(lexer2.tokenList);
            DefinitionNode node2 = parser2.parseDefinition(
                        dependent2,
                        Set.of(independent)
                    );
            plotManager.removePlot(plot);
            plot = new ParametricPlot(dependent1 + " " + dependent2, 
                t -> {
                    map.put(independent, t);
                    return node.evaluate(map);
                },
                t -> {
                    map.put(independent, t);
                    return node2.evaluate(map);
                },
                Double.parseDouble(minimum.getText()),
                Double.parseDouble(maximum.getText()), 
                Double.parseDouble(maxSamples.getText()),
                colorChooser.getSelectedColor());       
            plotManager.addPlot(plot);
        }catch(Exception e1){
            System.out.println(e1.getMessage());
        }
    }
}
