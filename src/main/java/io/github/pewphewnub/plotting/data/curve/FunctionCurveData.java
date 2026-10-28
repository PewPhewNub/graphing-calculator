package io.github.pewphewnub.plotting.data.curve;

import java.util.ArrayList;

import io.github.pewphewnub.parser.EvaluationContext;
import io.github.pewphewnub.plotting.data.Segment2D;
import io.github.pewphewnub.plotting.plots.FunctionPlot;
import javafx.geometry.Point2D;

public class FunctionCurveData extends CurveData{

    public FunctionCurveData(FunctionPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> featurePoints){
        super(plot, segments, featurePoints);
    }
    public FunctionCurveData(FunctionPlot plot){
        super(plot);
    }

    public Point2D targettedPoint(double mouseX, double mouseY, EvaluationContext context){
        return new Point2D(mouseX, ((FunctionPlot) originalPlot).getFunction(context).apply(mouseX));
    }

    @Override
    public FunctionCurveData copy(CurveData data) {
        FunctionCurveData newData = new FunctionCurveData((FunctionPlot)this.plot());
        newData.setFeaturePoints(featurePoints);
        newData.setVisibleSegments(visibleSegments);
        return newData;
    }
}
