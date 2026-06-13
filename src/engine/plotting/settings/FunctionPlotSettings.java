package engine.plotting.settings;

import java.util.Set;

public class FunctionPlotSettings implements PlotSettings{
    public String independentVariable;
    public String dependentVariable;

    @Override
    public Set<String> getDependents() {
        return Set.of(dependentVariable);
    }
    @Override
    public Set<String> getIndependents() {
        return Set.of(independentVariable);
    }

    public FunctionPlotSettings(){
        independentVariable = "x";
        dependentVariable = "y";
    }
}
