package plotting.plots;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import core.math.Core.Interval;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import plotting.data.ParametricCurveChunk;

public class ParametricPlot extends AbstractPlot implements CartesianPlot{
    public Function<Double, Double> x;
    public Function<Double, Double> y;
    public double tMin;
    public double tMax;
    public String expression1;
    public String expression2;
    public ArrayList<ParametricCurveChunk> chunks;
    public final Set<String> knownVariables = Set.of("t");
    public ParametricPlot(String name, String expression1, String expression2, Function<Double, Double> x, Function<Double, Double> y, double tMin, double tMax, Color color){
        this.x = x;
        this.y = y;
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.expression1 = expression1;
        this.expression2 = expression2;

        chunks = new ArrayList<>();    
        initializeChunks();
    }
    public ParametricPlot(String name, String expression1, String expression2, double tMin, double tMax, Color color){
        this.x = PlotGenerator.generateFunction(expression1, "x", "t");
        this.y = PlotGenerator.generateFunction(expression2, "y", "t");
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.expression1 = expression1;
        this.expression2 = expression2;

        chunks = new ArrayList<>();    
        initializeChunks();
    }
    public ParametricPlot(){
        this.x = t -> 2*t;
        this.y = t -> t*t;
        this.name = "New Parametric Plot";
        this.color = Color.RED;
        this.tMin = 0;
        this.tMax = 50;
        this.expression1 = "2*t";
        this.expression2 = "t^2";
        
        chunks = new ArrayList<>();    
        initializeChunks();
    }

    public void initializeChunks(){
        chunks.clear();
        double chunkSize = 5; // radians

        for(double t = tMin; t < tMax; t += chunkSize){
            chunks.add(
                new ParametricCurveChunk(
                    new Interval(t, Math.min(t + chunkSize, tMax)),
                    sample(t),
                    sample(Math.min(t + chunkSize, tMax)),
                    computeBounds(t, Math.min(t + chunkSize, tMax))
                )
            );
        }
    }

    public BoundingBox computeBounds(double t0, double t1){
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        int samples = 128;

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

    public Point2D sample(double t){
        return new Point2D(x.apply(t), y.apply(t));
    }

    public boolean update(
            String name,
            String expression1,
            String expression2,
            double minT,
            double maxT,
            Color color) {

        Function<Double, Double> newFunction1 =
                PlotGenerator.generateFunction(
                        expression1,
                        "x",
                        "t");
        Function<Double, Double> newFunction2 =
                PlotGenerator.generateFunction(
                        expression2,
                        "y",
                        "t");

        if(newFunction1 == null || newFunction2 == null)
            return false;

        this.expression1 = expression1;
        this.expression2 = expression2;
        this.color = color;
        this.x = newFunction1;
        this.y = newFunction2;
        this.tMax = maxT;
        this.tMin = minT;
        this.name = name;
        initializeChunks();

        return true;
    }

    @Override
    public ParametricPlot copy() {
        ParametricPlot p = new ParametricPlot(); 
        p.update(name, expression1, expression2, tMin, tMax, color);
        return p;
    }
    @Override
    public boolean copyFrom(AbstractPlot plot){
        if(plot == null) return false;
        if(plot instanceof ParametricPlot p){
            if(p.x == null) return false;
            if(p.y == null) return false;
            name = p.name;
            expression1 = p.expression1;
            expression2 = p.expression2;
            color = p.color;
            x = p.x;
            y = p.y;
            tMin = p.tMin;
            tMax = p.tMax;
            update();
            return true;
        }else{
            return false;
        }
    }
    @Override
    public void update() {
        initializeChunks();
    }

    @Override
    public boolean equals(AbstractPlot plot) {
        if(plot instanceof ParametricPlot p){
            return p.name.trim().equals(name.trim())&&
                   p.expression1.trim().equals(expression1.trim())&&
                   p.expression2.trim().equals(expression2.trim())&&
                   p.color.equals(color)&&
                   p.tMin == tMin &&
                   p.tMax == tMax; 
        }
        return false;
    }
}
