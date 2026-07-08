package plotting.data;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.BoundingBox;

public class ImplicitChunk{
    public BoundingBox bounds;

    public boolean generated = false;
    public boolean hasCurve = false;
    public int LOD;

    public ArrayList<Segment2D> segments;
    public final HashMap<Long, Double> sampleCache = new HashMap<>();
}