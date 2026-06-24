package engine.UI.controls;

import engine.UI.UIElements.CloseButton;
import engine.UI.UIElements.ColorChooser;
import engine.UI.UIElements.EquationInput;
import engine.UI.UIElements.LabelledInput;
import engine.UI.UIElements.MoreOptionsButton;
import engine.plotting.PlotManager;
import engine.plotting.plots.ParametricPlot;
import engine.plotting.plots.PlotGenerator;
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

public class ParametricPlotEditor extends PlotEditor{

    public ColorChooser colorChooser;
    public BorderPane topPanel;

    private EquationInput box0;
    private EquationInput box1;
    private LabelledInput box2;
    private LabelledInput box3;

    public String dependent1 = "x";
    public String dependent2 = "y";
    public String independent = "t";

    double minT = 0;
    double maxT = 50;

    private MoreOptionsButton advancedButton;
    private VBox advancedOptionsPanel;

    public ParametricPlotEditor(PlotManager plotManager){
        this.plotManager = plotManager;
        initialize();
        buildPlot();
    }

    public ParametricPlotEditor(PlotManager plotManager, ParametricPlot plot){
        this(plotManager);
        initialize();
        this.plot = plot;

        box0.setLabelText("x(t) = ");
        box0.setFieldText(plot.expression1);
        box1.setLabelText("y(t) = ");
        box1.setFieldText(plot.expression2);
        
        colorChooser.setSelectedColor(plot.getColor());

        box2.setText(Double.toString(plot.tMin));
        box3.setText(Double.toString(plot.tMax));
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
        colorChooser.colorProperty().addListener((obs, oldColor, newColor) -> {
            try {
                buildPlot();
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        });

        box0 = new EquationInput("x(t) = ", 14, "2t");
        box1 = new EquationInput("y(t) = ", 14, "t*t");

        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                buildPlot();
            }
        );
        box1.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                buildPlot();
            }
        );
        
        advancedOptionsPanel = new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        advancedButton = new MoreOptionsButton("\u25B6 Hide more options", "\u25BE Show more options", 9, advancedOptionsPanel);
        
        box2 = new LabelledInput("Minimum parameter value:", 9, "0", 14);
        box3 = new LabelledInput("Maximum parameter value:", 9, "50", 14);

        box2.textProperty().addListener((obs, oldValue, newValue) -> {
            minT = Double.parseDouble(newValue);
            buildPlot();
        });
        box3.textProperty().addListener((obs, oldValue, newValue) -> {
            maxT = Double.parseDouble(newValue);
            buildPlot();
        });


        this.getChildren().add(box0);
        this.getChildren().add(box1);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box2);
        advancedOptionsPanel.getChildren().add(box3);

        topPanel = new BorderPane();
        getChildren().add(0, topPanel);
        topPanel.setLeft(colorChooser);
        topPanel.setCenter(new Label("Function Plot"){
            {
                setAlignment(Pos.CENTER);
            }
        });

        CloseButton button = new CloseButton();
        button.setOnMouseClicked(e -> close());
        topPanel.setRight(button);
    }

    @Override
    public void buildPlot(){
        String text = box0.getText();
        String text2 = box1.getText();
        if(plot!=null) plotManager.removePlot(plot);
        plot = PlotGenerator.generateParametricPlot("name", text, text2, minT, maxT, colorChooser.getSelectedColor());
        if(plot!= null) plotManager.addPlot(plot);
    }
}
