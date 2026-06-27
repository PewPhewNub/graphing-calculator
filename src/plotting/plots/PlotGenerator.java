package plotting.plots;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.scene.paint.Color;
import parser.Lexer;
import parser.Parser;
import parser.node.DefinitionNode;

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

    public static ParametricPlot generateParametricPlot(String name, String expression1, String expression2, double tMin, double tMax, Color color){
        Lexer lexer = new Lexer(expression1);
        Lexer lexer2 = new Lexer(expression2);
        Map<String, Double> map = new HashMap<>();
        try {
            lexer.tokenize();
            lexer2.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = parser.parseDefinition(
                        "x",
                        Set.of("t")
                    );
            Parser parser2 = new Parser(lexer2.tokenList);
            DefinitionNode node2 = parser2.parseDefinition(
                        "y",
                        Set.of("t")
                    );
            ParametricPlot plot = new ParametricPlot(name,
                expression1,
                expression2, 
                t -> {
                    map.put("t", t);
                    return node.evaluate(map);
                },
                t -> {
                    map.put("t", t);
                    return node2.evaluate(map);
                },
                tMin,
                tMax, 
                color);
            if(plot!= null) return plot;
        }catch(Exception e1){
            System.out.println(e1.getMessage());
        }
        return null;
    }

    public static PolarPlot generatePolarPlot(String name, String expression, double tMin, double tMax, Color color){
        Lexer lexer = new Lexer(expression);
        Map<String, Double> map = new HashMap<>();
        try {
            lexer.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = parser.parseDefinition(
                        "x",
                        Set.of("t")
                    );
            PolarPlot plot = new PolarPlot(name,
                expression,
                t -> {
                    map.put("\u03B8", t);
                    return node.evaluate(map);
                },
                tMin,
                tMax, 
                color);
            if(plot!= null) return plot;
        }catch(Exception e1){
            System.out.println(e1.getMessage());
        }
        return null;
    }

    public static ImplicitPlot generateImplicitPlot(String name, String expression1, String expression2, Color color){
        String equivExpression = "(" +  expression2 + ") - (" + expression1 + ")";
        
        ImplicitPlot plot = new ImplicitPlot(name,
            expression1,
            expression2,
            generateBiFunction(equivExpression),
            color);
        return plot;
    }

    public static BiFunction<Double, Double, Double> generateBiFunction(String expression){
        Lexer lexer = new Lexer(expression);
        Map<String, Double> map = new HashMap<>();
        try {
            lexer.tokenize();
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = parser.parseDefinition(
                        "y",
                        Set.of("x")
                    );
            return (x, y) -> {
                    map.put("x", x);
                    map.put("y", y);
                    return node.evaluate(map);
            };
        }catch(Exception e1){
            
        }
        return null;
    }

}
