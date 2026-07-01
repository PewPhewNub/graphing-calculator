package plotting.data.curve;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate.Param;

import javafx.geometry.Point2D;
import parser.EvaluationContext;
import plotting.data.ParametricCurveChunk;
import plotting.data.Segment2D;
import plotting.plots.PolarPlot;

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
