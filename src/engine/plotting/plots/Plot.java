package engine.plotting.plots;

import javafx.scene.paint.Color;

public abstract class Plot {
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
}

