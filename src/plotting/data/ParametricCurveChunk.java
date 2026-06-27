package plotting.data;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import math.Interval;

public class ParametricCurveChunk {
    public BoundingBox bounds;
    public Interval parameterRange;
    public Point2D initial;
    public Point2D end;

    public ParametricCurveChunk(Interval parameterRange, Point2D initial, Point2D end, BoundingBox box){
        this.parameterRange = parameterRange;
        this.bounds = box;
        this.initial = initial;
        this.end = end;
    }
}
