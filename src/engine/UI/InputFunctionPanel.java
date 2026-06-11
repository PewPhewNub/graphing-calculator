package engine.UI;

import core.parser.Lexer;
import core.parser.Parser;
import core.parser.node.DefinitionNode;
import engine.plotting.FunctionPlot;
import engine.plotting.Plot;
import engine.rendering.Graph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class InputFunctionPanel extends VBox{
    ComboBox<PlotType> plotType;
    TextField field;
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
        plotType = new ComboBox<PlotType>();
        plotType.getItems().addAll(PlotType.values());
        plotType.setBorder(Border.EMPTY);
        plotType.setShape(new Rectangle(60, 20));
        plotType.setBackground(new Background(whiteBackgroundFill));

        field = new TextField();
        field.setFont(new Font(24));
        field.setAlignment(Pos.CENTER_LEFT);
        field.setOnKeyTyped(e -> {
            try {
                handleText(e);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        field.setBackground(new Background(whiteBackgroundFill));
        field.setBorder(thinBorder);

        getChildren().add(plotType);
        getChildren().add(field);
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
                text = "r(theta) = ";
                break;
            default:
                break;
        }
        field.setText(text);
    }

    public void handleText(KeyEvent e) throws Exception{
        for(Plot i : graph.plotManager.plots){
            if(i.getName().equals(lastValidName)){
                graph.plotManager.removePlot(i);
                break;
            }
        }
        
        String text = field.getText();
        
        Lexer lexer = new Lexer(text);
        DefinitionNode node = null;
        try {
            lexer.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            node = parser.parseDefinitionFunction();
            FunctionPlot plot = new FunctionPlot(node.getName(), node.getFunction(), Color.RED);

            for(Plot i : graph.plotManager.plots){
                if(i.getName().equals(plot.getName())){
                    return;
                }
            }
            lastValidName = plot.getName();
            graph.plotManager.addPlot(plot);

        } catch (Exception ex) {
            // TODO Auto-generated catch block
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
