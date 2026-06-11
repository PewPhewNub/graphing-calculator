package engine.UI;

import core.parser.Lexer;
import core.parser.Parser;
import core.parser.node.DefinitionNode;
import engine.plotting.FunctionPlot;
import engine.plotting.Plot;
import engine.plotting.PolarPlot;
import engine.rendering.Graph;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class InputFunctionPanel extends VBox{
    HBox topPanel;
    ComboBox<PlotType> plotType;
    ColorChooser colorPicker;
    Plot plot;

    VBox middlePanel;
    TextField field1;
    TextField field2;
    Button dropDownButton;

    VBox dropDownPanel;

    String lastValidName;
    Graph graph;

    public InputFunctionPanel(Graph graph){
        BackgroundFill whiteBackgroundFill = new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(2));
        Border thinBorder = new Border(new BorderStroke(
            Color.rgb(220, 220, 220),       // A soft, light gray color
            BorderStrokeStyle.SOLID,        // Solid line style
            CornerRadii.EMPTY,              // Perfectly square corners
            new BorderWidths(1)             // 1-pixel thickness
        ));
        setBackground(new Background(whiteBackgroundFill));
        
        this.graph = graph;
        this.plot = null;
        
        plotType = new ComboBox<PlotType>();
        plotType.setValue(PlotType.FunctionX);
        plotType.getItems().addAll(PlotType.values());
        plotType.setBorder(Border.EMPTY);
        plotType.setShape(new Rectangle(60, 20));
        plotType.setBackground(new Background(whiteBackgroundFill));

        colorPicker = new ColorChooser(Color.RED);
        colorPicker.colorProperty().addListener((obs, oldColor, newColor) -> {
            if(plot != null) plot.setColor(newColor);
        });

        topPanel = new HBox();
        topPanel.getChildren().add(0, plotType);
        topPanel.getChildren().add(1, colorPicker);

        field1 = new TextField("y = ");
        field1.setFont(new Font(24));
        field1.setAlignment(Pos.CENTER_LEFT);
        field1.setOnKeyTyped(e -> {
            try {
                handleText(e);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        field1.setBackground(new Background(whiteBackgroundFill));
        field1.setBorder(thinBorder);

        getChildren().add(topPanel);
        getChildren().add(field1);
        plotType.setOnAction(e -> handleExtra());
    }

    public void handleExtra(){
        String text = "";
        switch ((PlotType)plotType.getValue()) {
            case FunctionX:
                text = "y = ";
                break;
            case ODE_RK4:
                text = "dy/dx = ";
                break;
            case Parametric:
                text = "((x(t) = ),(y(t) = ))";
                break;
            case Polar:
                text = "r = ";
                break;
            default:
                break;
        }
        field1.setText(text);
    }

    public void handleText(KeyEvent e) throws Exception{
        for(Plot i : graph.plotManager.plots){
            if(i.getName().equals(lastValidName)){
                graph.plotManager.removePlot(i);
                plot = null;
                break;
            }
        }
        
        String text = field1.getText();
        
        Lexer lexer = new Lexer(text);
        DefinitionNode node = null;
        try {
            lexer.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            if(plotType.getValue() == PlotType.Polar){
                node = parser.parseDefinitionFunction();
                plot = new PolarPlot(node.getName(), node.getFunction(), 0, 100, 50000, colorPicker.getSelectedColor());
            }else{
                node = parser.parseDefinitionFunction();
                plot = new FunctionPlot(node.getName(), node.getFunction(), colorPicker.getSelectedColor());    
            }
            for(Plot i : graph.plotManager.plots){
                if(i.getName().equals(plot.getName())){
                    return;
                }
            }
            lastValidName = plot.getName();
            graph.plotManager.addPlot(plot);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 }
enum PlotType{
    FunctionX,
    ODE_RK4,
    Parametric,
    Polar
}
