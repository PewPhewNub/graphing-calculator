package engine.plotting.plots;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import core.parser.Lexer;
import core.parser.Parser;
import core.parser.node.DefinitionNode;
import javafx.scene.paint.Color;

public class PlotGenerator {
    public static FunctionPlot generateFunctionPlot(String name, String expression, String dependent, String independent, Color color){
        Function<Double, Double> function = generateFunction(expression, dependent, independent);
        if(function == null) return null;
        return new FunctionPlot(name, expression, function, color);
    }
    public static Function<Double, Double> generateFunction(String expression, String dependent, String independent){
        Lexer lexer = new Lexer(expression);
        Map<String, Double> map = new HashMap<>();
        try {
            lexer.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = parser.parseDefinition(
                        dependent,
                        Set.of(independent)
                    );
            return x -> {
                    map.put(independent, x);
                    return node.evaluate(map);
            };
        }catch(Exception e1){
            System.out.println("bruh");
        }
        return null;
    }
}
