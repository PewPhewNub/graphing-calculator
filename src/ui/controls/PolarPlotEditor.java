package ui.controls;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import parser.Lexer;
import parser.Parser;
import parser.node.DefinitionNode;
import plotting.PlotManager;
import plotting.plots.FunctionPlot;
import plotting.plots.PlotGenerator;
import plotting.plots.PolarPlot;
import ui.components.CloseButton;
import ui.components.ColorChooser;
import ui.components.EquationInput;
import ui.components.LabelledInput;
import ui.components.MoreOptionsButton;

public class PolarPlotEditor extends PlotEditor{
    public ColorChooser colorChooser;
    public BorderPane topPanel;

    public String dependent = "r";
    public String independent = "\u03B8";

    private EquationInput box0;
    private LabelledInput box1;
    private LabelledInput box2;

    double minT = 0;
    double maxT = 50;

    private MoreOptionsButton advancedButton;
    private VBox advancedOptionsPanel;
    private PolarPlot plot;

    public PolarPlotEditor(PlotManager plotManager, PolarPlot plot){
        this.plotManager = plotManager;
        initialize();
        this.plot = plot;

        box0.setLabelText("r(\u03B8) = ");
        box0.setFieldText(plot.expression);
        
        colorChooser.setSelectedColor(plot.getColor());

        box1.setText(Double.toString(plot.tMin));
        box2.setText(Double.toString(plot.tMax));

        addHandlers();
    }

    private void addHandlers(){
        colorChooser.colorProperty().addListener((obs, oldColor, newColor) -> {
            buildPlot();
        });
        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                buildPlot();
            }
        );
        box1.textProperty().addListener((obs, oldValue, newValue) -> {
            minT = Double.parseDouble(newValue);
            if(Double.isNaN(minT)) minT = 0;
            buildPlot();
        });
        box2.textProperty().addListener((obs, oldValue, newValue) -> {
            maxT = Double.parseDouble(newValue);
            if(Double.isNaN(maxT)) maxT = 0;
            buildPlot();
        });
    }
    private void initialize(){  
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

        box0 = new EquationInput("r(\u03B8) = ", 14, "\u03B8");

        advancedOptionsPanel =new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        advancedButton = new MoreOptionsButton("\u25B6 Hide more options", "\u25BE Show more options", 9, advancedOptionsPanel);
        
        box1 = new LabelledInput("Minimum parameter value:", 9, "0", 14);
        box2 = new LabelledInput("Maximum parameter value:", 9, "50", 14);

        this.getChildren().add(box0);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box1);
        advancedOptionsPanel.getChildren().add(box2);

        topPanel = new BorderPane();
        getChildren().add(0, topPanel);
        topPanel.setLeft(colorChooser);
        topPanel.setCenter(new Label("Polar Plot"){
            {
                setAlignment(Pos.CENTER);
            }
        });

        CloseButton button = new CloseButton();
        button.setOnMouseClicked(e -> close());
        topPanel.setRight(button);
    }
    
    @Override
    protected void buildPlot(){
        if(((PolarPlot)plot).update(
            box0.getText(),
            minT,
            maxT,
            colorChooser.getSelectedColor()
        )){
            plotManager.plotChanged(plot);
        }
    }
}
