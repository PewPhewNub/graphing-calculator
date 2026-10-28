package io.github.pewphewnub.plotting.data.curve;

import java.util.ArrayList;

import io.github.pewphewnub.parser.EvaluationContext;
import io.github.pewphewnub.plotting.data.ParametricCurveChunk;
import io.github.pewphewnub.plotting.data.Segment2D;
import io.github.pewphewnub.plotting.plots.PolarPlot;
import javafx.geometry.Point2D;

public class PolarCurveData extends CurveData{
    public ArrayList<ParametricCurveChunk> chunks;
    public PolarCurveData(PolarPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
        chunks = new ArrayList<>();
    }
    public PolarCurveData(PolarPlot plot){
        super(plot);
        chunks = new ArrayList<>();
    }
    public void setChunks(ArrayList<ParametricCurveChunk> chunks){
        this.chunks = new ArrayList<>(chunks);
    }

    public Point2D targettedPoint(double mouseX, double mouseY, EvaluationContext context){
        return nearestPoint(mouseX, mouseY);
    }
    @Override
    public PolarCurveData copy(CurveData data) {
        if(!(data instanceof PolarCurveData)) return null;
        PolarCurveData newData = new PolarCurveData((PolarPlot)this.plot());
        newData.setChunks(chunks);
        newData.setFeaturePoints(featurePoints);
        newData.setVisibleSegments(visibleSegments);
        return newData;
    }
}
