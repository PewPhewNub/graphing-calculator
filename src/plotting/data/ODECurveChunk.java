package plotting.data;

import core.math.Core.Interval;
import core.math.Core.Point;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

public class ODECurveChunk {
    public BoundingBox bounds;
    public int initial;
    public int end;
    
    public ODECurveChunk(int initial, int end, BoundingBox box){
        this.bounds = box;
        this.initial = initial;
        this.end = end;
    }
}
