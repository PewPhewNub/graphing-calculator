package engine.UI.controls;

import engine.UI.UIElements.CloseButton;
import engine.UI.UIElements.ColorChooser;
import engine.UI.UIElements.EquationInput;
import engine.UI.UIElements.LabelledInput;
import engine.UI.UIElements.MoreOptionsButton;
import engine.plotting.PlotManager;
import engine.plotting.plots.FunctionPlot;
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

public class FunctionPlotEditor extends PlotEditor{

    public ColorChooser colorChooser;
    public BorderPane topPanel;

    public String dependent = "y";
    public String independent = "x";

    private EquationInput box0;
    private LabelledInput box1;
    private LabelledInput box2;

    private MoreOptionsButton advancedButton;
    private VBox advancedOptionsPanel;

    private FunctionPlot plot;

    public FunctionPlotEditor(PlotManager plotManager){
        this.plotManager = plotManager;
        initialize();
        buildPlot();
    }
    public FunctionPlotEditor(PlotManager plotManager, FunctionPlot plot){
        this.plotManager = plotManager;
        initialize();
        this.plot = plot;
        dependent = plot.dependent;
        independent = plot.independent;

        box0.setLabelText(dependent + "(" + independent + ") = ");
        box0.setFieldText(plot.expression);

        box1.setText(independent);
        box2.setText(dependent);
    }

    private void initialize(){
        this.plot = null;

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

        box0 = new EquationInput(dependent + "(" + independent + ") = ", 14, independent);
        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                buildPlot();
            }
        );

        advancedOptionsPanel = new VBox();
        advancedOptionsPanel.setVisible(false);
        advancedOptionsPanel.setManaged(false);

        advancedButton = new MoreOptionsButton("\u25B6 Hide more options", "\u25BE Show more options", 9, advancedOptionsPanel);

        box1 = new LabelledInput("Independent Variable:", 9, "x", 14);
        box2 = new LabelledInput("Dependent Variable:", 9, "y", 14);
        
        box1.textProperty().addListener((obs, oldValue, newValue) -> {
            independent = box1.getText();
            box0.setLabelText(dependent + "(" + independent + ") = ");
            buildPlot();
        });

        box2.textProperty().addListener((obs, oldValue, newValue) -> {
            dependent = box2.getText();
            box0.setLabelText(dependent + "(" + independent + ") = ");
            buildPlot();
        });

        this.getChildren().add(box0);
        this.getChildren().add(advancedButton);
        this.getChildren().add(advancedOptionsPanel);
        advancedOptionsPanel.getChildren().add(box1);
        advancedOptionsPanel.getChildren().add(box2);

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
    protected void buildPlot(){
        String text = box0.getText();
        if(plot!=null) plotManager.removePlot(plot);
        plot = PlotGenerator.generateFunctionPlot("name", text, dependent, independent, colorChooser.getSelectedColor());
        if(plot!= null) plotManager.addPlot(plot);
    }
}
