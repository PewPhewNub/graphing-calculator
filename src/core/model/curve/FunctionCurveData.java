package core.model.curve;

import java.util.ArrayList;

import core.model.Segment2D;
import engine.interaction.InteractionResult;
import engine.plotting.plots.FunctionPlot;
import javafx.geometry.Point2D;

public class FunctionCurveData extends CurveData{

    public FunctionCurveData(FunctionPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
    }

    public Point2D targettedPoint(double mouseX, double mouseY){
        return new Point2D(mouseX, ((FunctionPlot) originalPlot).getFunction().apply(mouseX));
    }
}
