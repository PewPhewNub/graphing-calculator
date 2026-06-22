package engine.UI.controls;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import core.parser.Lexer;
import core.parser.Parser;
import core.parser.node.DefinitionNode;
import engine.UI.UIElements.CloseButton;
import engine.UI.UIElements.ColorChooser;
import engine.UI.UIElements.EquationInput;
import engine.plotting.PlotManager;
import engine.plotting.plots.ImplicitPlot;
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
import javafx.scene.paint.Color;

public class ImplicitPlotEditor extends PlotEditor{

    public ColorChooser colorChooser;
    public BorderPane topPanel;

   private EquationInput box0;

    public String dependent = "y";
    public String independent = "x";

    public ImplicitPlotEditor(PlotManager plotManager){
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

        box0 = new EquationInput("", 14, "y = x");
        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                buildPlot();
            }
        );
   
        this.getChildren().add(box0);

        topPanel = new BorderPane();
        getChildren().add(0, topPanel);
        topPanel.setLeft(colorChooser);
        topPanel.setCenter(new Label("Implicit Plot"){
            {
                setAlignment(Pos.CENTER);
            }
        });

        CloseButton button = new CloseButton();
        button.setOnMouseClicked(e -> close());
        topPanel.setRight(button);

        buildPlot();
    }

    @Override
    public void buildPlot() {
        String text = box0.getText();
        
        Lexer lexer = new Lexer(text);
        Map<String, Double> map = new HashMap<>();
        try {
            lexer.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = parser.parseDefinition(
                        dependent,
                        Set.of(independent)
                    );
            plotManager.removePlot(plot);
            plot = new ImplicitPlot(dependent, 
                (x, y) -> {
                    map.put(independent, x);
                    map.put(dependent, y);
                    return node.evaluate(map);
                }, colorChooser.getSelectedColor());       
            plotManager.addPlot(plot);
        }catch(Exception e1){
            System.out.println(e1.getMessage());
        }
    }
}
