package plotting.plots;

import javafx.scene.paint.Color;

public abstract class AbstractPlot {
    String name;
    Color color;

    public String getName(){
        return name;
    }
    public Color getColor(){
        return color;
    }
    public void setColor(Color color){
        this.color = color;
    }
    public abstract AbstractPlot copy();
    public abstract boolean copyFrom(AbstractPlot other);
    public abstract void update();
    public abstract boolean equals(AbstractPlot plot);
}

