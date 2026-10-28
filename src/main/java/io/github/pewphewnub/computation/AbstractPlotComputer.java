package io.github.pewphewnub.computation;

import io.github.pewphewnub.parser.EvaluationContext;
import io.github.pewphewnub.plotting.data.GridData;
import io.github.pewphewnub.plotting.data.curve.CurveData;
import io.github.pewphewnub.plotting.plots.AbstractPlot;
import io.github.pewphewnub.rendering.camera.Viewport;

public abstract class AbstractPlotComputer<P extends AbstractPlot, D extends CurveData>{
    protected final P plot;
    protected final D data;

    public AbstractPlotComputer(P plot, D data){
        this.plot = plot;
        this.data = data;
    }
    protected abstract void ensureCoverage(Viewport viewport, EvaluationContext context);
    protected abstract void generateCurveData(Viewport viewport, GridData gridData, EvaluationContext context);
    public abstract void invalidate();
    public P getPlot() {
        return plot;
    }
    public D getData() {
        return data;
    }
}
