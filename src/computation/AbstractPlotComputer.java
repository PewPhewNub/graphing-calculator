package computation;

import parser.EvaluationContext;
import plotting.data.GridData;
import plotting.data.curve.CurveData;
import plotting.plots.AbstractPlot;
import rendering.camera.Viewport;

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
