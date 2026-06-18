package core.model;

import core.math.Core.Interval;
import javafx.geometry.BoundingBox;

public class CurveChunk {
    public BoundingBox bounds;
    public Interval parameterRange;

    public CurveChunk(Interval parameterRange, BoundingBox box){
        this.parameterRange = parameterRange;
        this.bounds = box;
    }

}
