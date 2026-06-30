package plotting.plots;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import math.Interval;
import parser.EvaluationContext;
import parser.ParseException;
import parser.node.DefinitionNode;
import plotting.GraphElement;
import plotting.data.ParametricCurveChunk;

public class PolarPlot extends AbstractPlot implements CartesianPlot{
    public double tMin;
    public double tMax;
    public String expression;
    private DefinitionNode definition;
    public ArrayList<ParametricCurveChunk> chunks;
    public String dependent = "r";
    public String independent = "\u03B8";

    public ArrayList<Point2D> currentList;
    public PolarPlot(String name, String expression, double tMin, double tMax, Color color) throws ParseException{
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.expression = expression;
        this.definition = PlotGenerator.generateDefinition(expression, dependent, Set.of(independent));
        chunks = new ArrayList<>();
    }
    
    public PolarPlot(){
        this.name = "New Polar Plot";
        this.color = Color.RED;
        this.tMin = 0;
        this.tMax = 50;
        this.expression = "\u03B8";
        try {
            definition = PlotGenerator.generateDefinition(expression, dependent, Set.of(independent));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        chunks = new ArrayList<>();
    }

    public void reloadChunks(EvaluationContext context){
        double chunkSize = 2; // radians

        for(double t = tMin; t < tMax; t += chunkSize){
            chunks.add(
                new ParametricCurveChunk(
                    new Interval(t, Math.min(t + chunkSize, tMax)),
                    sample(t, context),
                    sample(Math.min(t + chunkSize, tMax), context),
                    computeBounds(t, Math.min(t + chunkSize, tMax), context)
                )
            );
        }
    }

    public BoundingBox computeBounds(double t0, double t1, EvaluationContext context){
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        int samples = 128;
        Function<Double, Double> x = t -> sampleX(t, context);
        Function<Double, Double> y = t -> sampleY(t, context);

        for(int i=0;i<=samples;i++){
            double t = t0 + (t1-t0)*i/(double)samples;

            double xt = x.apply(t);
            double yt = y.apply(t);

            minX = Math.min(minX, xt);
            minY = Math.min(minY, yt);
            maxX = Math.max(maxX, xt);
            maxY = Math.max(maxY, yt);
        }

        return new BoundingBox(minX,minY,maxX - minX,maxY - minY);
    }

    public Point2D sample(double t, EvaluationContext context){
        return new Point2D(sampleX(t, context), sampleY(t, context));
    }
    public double sampleR(double t, EvaluationContext context){
        context.set(independent, t);
        return definition.evaluate(context);
    }
    public double sampleX(double t, EvaluationContext context){
        return sampleR(t, context)* Math.cos(t);
    }
    public double sampleY(double t, EvaluationContext context){
        return sampleR(t, context)* Math.sin(t);
    }

    @Override
    public PolarPlot copy() {
        try{
            return new PolarPlot(name, expression, tMin, tMax, color);
        }catch(ParseException p){
            p.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean copyFrom(GraphElement plot){
        if(plot == null) return false;
        if(plot instanceof PolarPlot p){
            if(p.definition == null) return false;
            name = p.name;
            expression = p.expression;
            color = p.color;
            tMin = p.tMin;
            tMax = p.tMax;
            return true;
        }else{
            return false;
        }
    }
    @Override
    public void update(EvaluationContext context) {
        reloadChunks(context);
    }
    @Override
    public boolean equals(AbstractPlot plot) {
        if(plot instanceof PolarPlot p){
            return p.name.trim().equals(name.trim())&&
                   p.expression.trim().equals(expression.trim())&&
                   p.color.equals(color)&&
                   p.tMin == tMin &&
                   p.tMax == tMax;
        }
        return false;
    }

    @Override
    public Set<String> getReferencedVariables() {
        return definition.getVariables();    
    }

    public Function<Double, Double> getX(EvaluationContext context){
        return t -> sampleX(tMax, context);
    }
    public Function<Double, Double> getY(EvaluationContext context){
        return t -> sampleY(tMax, context);
    }
}
