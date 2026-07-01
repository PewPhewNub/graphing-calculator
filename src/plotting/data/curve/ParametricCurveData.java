package plotting.data.curve;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import parser.EvaluationContext;
import plotting.data.ParametricCurveChunk;
import plotting.data.Segment2D;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;

public class ParametricCurveData extends CurveData{
    public ArrayList<ParametricCurveChunk> chunks;
    public ParametricCurveData(ParametricPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
        chunks = new ArrayList<>();
    }
    public ParametricCurveData(ParametricPlot plot){
        super(plot);
        chunks = new ArrayList<>();
    }
    public void setChunks(ArrayList<ParametricCurveChunk> chunks) {
        this.chunks = chunks;
    }
    public Point2D targettedPoint(double mouseX, double mouseY, EvaluationContext context){
        return nearestPoint(mouseX, mouseY);
    }
    @Override
    public CurveData copy(CurveData data) {
        if(!(data instanceof ParametricCurveData)) return null;
        ParametricCurveData newData = new ParametricCurveData((ParametricPlot)this.plot());
        newData.setChunks(chunks);
        newData.setFeaturePoints(featurePoints);
        newData.setVisibleSegments(visibleSegments);
        return newData;
    }
}
