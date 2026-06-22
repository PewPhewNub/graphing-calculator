package core.model.curve;

import java.util.ArrayList;

import core.model.Segment2D;
import engine.plotting.plots.ImplicitPlot;
import javafx.geometry.Point2D;

public class ImplicitCurveData extends CurveData{
    public ImplicitCurveData(ImplicitPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
    }

    public Point2D targettedPoint(double mouseX, double mouseY){
        return nearestPoint(mouseX, mouseY);
    }
}
