package plotting.data.curve;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import parser.EvaluationContext;
import plotting.data.Segment2D;
import plotting.plots.ImplicitPlot;

public class ImplicitCurveData extends CurveData{
    public ImplicitCurveData(ImplicitPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
    }
    public ImplicitCurveData(ImplicitPlot plot){
        super(plot);
    }

    public Point2D targettedPoint(double mouseX, double mouseY, EvaluationContext context){
        return nearestPoint(mouseX, mouseY);
    }
    @Override
    public CurveData copy(CurveData data) {
        ImplicitCurveData newData = new ImplicitCurveData((ImplicitPlot)plot());
        newData.setFeaturePoints(featurePoints);
        newData.setVisibleSegments(visibleSegments);
        return newData;
    }
}
