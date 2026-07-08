package plotting.data.curve;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.Point2D;
import parser.EvaluationContext;
import plotting.data.ImplicitChunk;
import plotting.data.Segment2D;
import plotting.plots.ImplicitPlot;

public class ImplicitCurveData extends CurveData{
    public HashMap<Point2D, ImplicitChunk> chunks;
    public ImplicitCurveData(ImplicitPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
        chunks = new HashMap<>();
    }
    public ImplicitCurveData(ImplicitPlot plot){
        super(plot);
        chunks = new HashMap<>();
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
