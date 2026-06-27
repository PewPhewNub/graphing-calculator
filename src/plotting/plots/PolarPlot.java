package plotting.plots;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import math.Interval;
import parser.ParseException;
import plotting.data.ParametricCurveChunk;

public class PolarPlot extends AbstractPlot implements CartesianPlot{
    public Function<Double, Double> r;
    public Function<Double, Double> x;
    public Function<Double, Double> y;
    public double tMin;
    public double tMax;
    public String expression;
    public ArrayList<ParametricCurveChunk> chunks;
    public final Set<String> knownVariables = Set.of("\u03B8");

    public ArrayList<Point2D> currentList;
    public PolarPlot(String name, String expression, Function<Double, Double> r, double tMin, double tMax, Color color){
        this.r = r;
        x = t-> r.apply(t)*Math.cos(t);
        y = t-> r.apply(t)*Math.sin(t);
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.expression = expression;
        chunks = new ArrayList<>();
        initializeChunks();
    }
    public PolarPlot(String name, String expression, double tMin, double tMax, Color color){
        try {
            this.r = PlotGenerator.generateFunction(expression, "r", "\u03B8");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        x = t-> r.apply(t)*Math.cos(t);
        y = t-> r.apply(t)*Math.sin(t);
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.expression = expression;
        chunks = new ArrayList<>();
        initializeChunks();
    }
    
    public PolarPlot(){
        this.r = t -> t;
        x = t-> r.apply(t)*Math.cos(t);
        y = t-> r.apply(t)*Math.sin(t);
        this.name = "New Polar Plot";
        this.color = Color.RED;
        this.tMin = 0;
        this.tMax = 50;
        this.expression = "\u03B8";
        chunks = new ArrayList<>();
        initializeChunks();
    }

    public void initializeChunks(){
        double chunkSize = 2; // radians

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
            String expression,
            double minT,
            double maxT,
            Color color) {

        Function<Double, Double> newFunction1;
        try {
            newFunction1 = PlotGenerator.generateFunction(
                    expression,
                    "r",
                    "\u03B8");
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        this.expression = expression;
        this.color = color;
        this.r = newFunction1;
        this.x = t -> r.apply(t)*Math.cos(t);
        this.y = t -> r.apply(t)*Math.sin(t);
        tMax = maxT;
        tMin = minT;
        this.name = name;
        initializeChunks();

        return true;
    }

    @Override
    public PolarPlot copy() {
        PolarPlot p = new PolarPlot(); 
        p.update(name, expression, tMin, tMax, color);
        return p;
    }
    @Override
    public boolean copyFrom(AbstractPlot plot){
        if(plot == null) return false;
        if(plot instanceof PolarPlot p){
            if(p.x == null) return false;
            if(p.y == null) return false;
            name = p.name;
            expression = p.expression;
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
        if(plot instanceof PolarPlot p){
            return p.name.trim().equals(name.trim())&&
                   p.expression.trim().equals(expression.trim())&&
                   p.color.equals(color)&&
                   p.tMin == tMin &&
                   p.tMax == tMax;
        }
        return false;
    }
}
