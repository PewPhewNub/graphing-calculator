package plotting.data.curve;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import plotting.data.Segment2D;
import plotting.plots.ParametricPlot;

public class ParametricCurveData extends CurveData{
    public ParametricCurveData(ParametricPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
    }

    public Point2D targettedPoint(double mouseX, double mouseY){
        return nearestPoint(mouseX, mouseY);
    }
}
