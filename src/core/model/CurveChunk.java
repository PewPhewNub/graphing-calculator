package core.model;

import core.math.Core.Interval;
import core.math.Core.Point;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

public class CurveChunk {
    public BoundingBox bounds;
    public Interval parameterRange;
    public int initial;
    public int end;

    public CurveChunk(Interval parameterRange, BoundingBox box){
        this.parameterRange = parameterRange;
        this.bounds = box;
        initial = 0;
        end = 0;
    }
    
    public CurveChunk(int initial, int end, BoundingBox box){
        this.bounds = box;
        this.initial = initial;
        this.end = end;
        parameterRange = null;
    }
}
