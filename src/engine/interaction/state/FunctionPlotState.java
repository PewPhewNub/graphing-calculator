package engine.interaction.state;

import javafx.scene.paint.Color;

public class FunctionPlotState extends PlotState{
    public final String expression;
    public final String dependent;
    public final String independent;
    
    public FunctionPlotState(String name, String expression, String independent, String dependent, Color color){
        super(name, color);
        this.expression = expression;
        this.dependent = dependent;
        this.independent = independent;
    }
}
