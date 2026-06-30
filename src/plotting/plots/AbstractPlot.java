package plotting.plots;

import java.util.Set;

import javafx.scene.paint.Color;
import parser.EvaluationContext;
import plotting.GraphElement;

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
    public abstract void update(EvaluationContext context);
    public abstract boolean equals(AbstractPlot plot);
    public abstract Set<String> getReferencedVariables();
}

