package plotting.data.curve;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import plotting.data.Segment2D;
import plotting.plots.FunctionPlot;

public class FunctionCurveData extends CurveData{

    public FunctionCurveData(FunctionPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
    }

    public Point2D targettedPoint(double mouseX, double mouseY){
        return new Point2D(mouseX, ((FunctionPlot) originalPlot).getFunction().apply(mouseX));
    }
}
