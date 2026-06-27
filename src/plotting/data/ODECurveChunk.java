package plotting.data;

import javafx.geometry.BoundingBox;

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
