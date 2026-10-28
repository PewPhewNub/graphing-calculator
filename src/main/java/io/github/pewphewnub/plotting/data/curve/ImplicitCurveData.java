package io.github.pewphewnub.plotting.data.curve;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.pewphewnub.parser.EvaluationContext;
import io.github.pewphewnub.plotting.data.ImplicitChunk;
import io.github.pewphewnub.plotting.data.Segment2D;
import io.github.pewphewnub.plotting.plots.ImplicitPlot;
import javafx.geometry.Point2D;

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
