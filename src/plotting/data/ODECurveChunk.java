package plotting.data;

import java.util.ArrayList;

import javafx.geometry.BoundingBox;

public class ODECurveChunk {
    public BoundingBox bounds;
    public int initial;
    public int end;
    public ArrayList<Segment2D> segments = new ArrayList<>();
    
    public ODECurveChunk(int initial, int end, BoundingBox box){
        this.bounds = box;
        this.initial = initial;
        this.end = end;
        this.segments = new ArrayList<>();
    }

    public ArrayList<Segment2D> getSegments() {
        return segments;
    }
    public void setSegments(ArrayList<Segment2D> segments) {
        this.segments = segments;
    }
}
