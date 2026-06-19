package engine.UI.controls;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import core.math.Core.Point;
import core.parser.Lexer;
import core.parser.Parser;
import core.parser.node.DefinitionNode;
import engine.UI.ColorChooser;
import engine.plotting.PlotManager;
import engine.plotting.plots.ODEPlot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

public class ODEPlotEditor extends PlotEditor{

    public ColorChooser colorChooser;
    public BorderPane topPanel;

    public String inputFunction;
    public HBox functionInputPanel;
    public TextField functionInputField;
    public Label functionInputLabel;

    public String dependent = "y";
    public String independent = "x";

    private Button advancedButton;
    private boolean isAdvancedShow = false;
    private VBox advancedOptionsPanel;

    private Label independentVarLabel;
    private Label dependentVarLabel;
    private TextField independentVarField;
    private TextField dependentVarField;

    private Button generate;
    private CheckBox autoGenerate;
    private CheckBox slopeField;

    public ODEPlotEditor(PlotManager plotManager){
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
            if(plot != null) plot.setColor(newColor);
        });

        functionInputPanel = new HBox();
        functionInputLabel = new Label("d(y)/d(x) = ");
        functionInputLabel.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
        )));
        functionInputLabel.setFont(new Font(20));
        functionInputLabel.setTextAlignment(TextAlignment.LEFT);
        functionInputLabel.setPadding(new Insets(5,0,5,15));

        functionInputField = new TextField("y");
        functionInputField.setFont(new Font(20));
        functionInputField.setAlignment(Pos.CENTER_LEFT);
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
                    int caretPosition = independentVarField.getCaretPosition();
                    
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

        generate = new Button("Generate Solution");
        generate.setFont(new Font(10));
        generate.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(3),
                new Insets(0)
            )));
        generate.setBorder(Border.EMPTY);
        generate.setOnAction(e -> {
            try {
                buildPlot();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });

        generate.setPadding(new Insets(5, 15, 5, 15));

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
        HBox box2 = new HBox();
        box1.setPadding(new Insets(5, 25, 5, 25));
        box2.setPadding(new Insets(5, 25, 5, 25));

        independentVarLabel = new Label("Independent Variable :");
        dependentVarLabel = new Label("Dependent Variable   :");
        independentVarLabel.setPadding(new Insets(5, 5, 5, 10));
        dependentVarLabel.setPadding(new Insets(5, 5, 5, 10));
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
        dependentVarLabel.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(2, 0, 0, 2, false),
            new Insets(2, 0, 2, 2)
        )));
        
        dependentVarLabel.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(2, 0, 0, 2, false),              // Perfectly square corners
            new BorderWidths(2, 0, 2, 2)             // 1-pixel thickness
        )));
        
        box1.getChildren().add(independentVarLabel);
        independentVarField = new TextField("x");

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
            functionInputLabel.setText("d(" + (dependent) + ")/d(" + independent + ") = ");
        });

        box1.getChildren().add(independentVarField);

        box2.getChildren().add(dependentVarLabel);
        dependentVarField = new TextField();
        dependentVarField.setText("y");

        dependentVarField.setBackground(new Background(new BackgroundFill(
            Color.WHITE,
            new CornerRadii(0, 2, 2, 0, false),
            new Insets(2, 2, 2, 0)
        )));
        
        dependentVarField.setBorder(new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            new CornerRadii(0, 2, 2, 0, false),              // Perfectly square corners
            new BorderWidths(2, 2, 2, 0)             // 1-pixel thickness
        )));
        dependentVarField.setPadding(new Insets(5, 0, 5, 0));

        dependentVarField.textProperty().addListener((obs, oldValue, newValue) -> {
            dependent = dependentVarField.getText();
            functionInputLabel.setText("d(" + (dependent) + ")/d(" + independent + ") = ");
        });

        box2.getChildren().add(dependentVarField);

        this.getChildren().add(functionInputPanel);
        this.getChildren().add(generate);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box1);
        advancedOptionsPanel.getChildren().add(box2);

        topPanel = new BorderPane();
        getChildren().add(0, topPanel);
        topPanel.setLeft(colorChooser);
        topPanel.setCenter(new Label("ODE Plot"){
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

        autoGenerate = new CheckBox("Auto-Generate Solution upon loading chunks");
        autoGenerate.setFont(new Font(12));
        autoGenerate.setSelected(false);
        autoGenerate.setOnAction(e ->{
            if(plot != null) ((ODEPlot)plot).setAutoGenerate(autoGenerate.isSelected());
        });
        autoGenerate.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        autoGenerate.setPadding(new Insets(5, 28, 5, 25));
        advancedOptionsPanel.getChildren().add(autoGenerate);
        
        slopeField = new CheckBox("Show Slope Fields");
        slopeField.setFont(new Font(12));
        slopeField.setSelected(false);
        slopeField.setOnAction(e ->{
            if(plot != null) ((ODEPlot)plot).setShowSlopeField(slopeField.isSelected());
        });
        slopeField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        slopeField.setPadding(new Insets(5, 28, 5, 25));
        advancedOptionsPanel.getChildren().add(slopeField);

        try {
            buildPlot();
        } catch(Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    @Override
    public void buildPlot() throws Exception {
        String text = functionInputField.getText();
        
        Lexer lexer = new Lexer(text);
        Map<String, Double> map = new HashMap<>();
        try {
            lexer.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = parser.parseDefinition(
                        dependent,
                        Set.of(independent, dependent)
                    );
            plotManager.removePlot(plot);
            plot = new ODEPlot(dependent, 
                (x, y) -> {
                    map.put(independent, x);
                    map.put(dependent, y);
                    return node.evaluate(map);
                }, new Point(0, 1), colorChooser.getSelectedColor());       
            plotManager.addPlot(plot);
            ((ODEPlot)plot).setShowSlopeField(slopeField.isSelected());
        }catch(Exception e1){
            System.out.println(e1.getMessage());
        }
    }
}
