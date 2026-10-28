package io.github.pewphewnub.plotting.plots;

import java.util.Set;

import io.github.pewphewnub.plotting.GraphElement;
import javafx.scene.paint.Color;

public abstract class AbstractPlot extends GraphElement{
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
    public abstract boolean equals(AbstractPlot plot);
    public abstract Set<String> getReferencedVariables();
}
