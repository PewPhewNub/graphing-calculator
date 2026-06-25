package plotting.data;

import java.util.ArrayList;

import javafx.geometry.BoundingBox;

public class ImplicitChunk{
    public BoundingBox bounds;

    public boolean generated = false;
    public boolean hasCurve = false;
    public int LOD;

    public ArrayList<Segment2D> segments;
}