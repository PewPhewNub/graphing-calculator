package plotting.plots;

import math.Point;

public interface ODECapable{
    public boolean showSlopeField();
    public void setShowSlopeField(boolean show);

    public void setInitialPoint(Point point);
    public Point getInitialPoint();
}
