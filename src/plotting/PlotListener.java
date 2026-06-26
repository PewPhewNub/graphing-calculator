package plotting;

import plotting.plots.AbstractPlot;

public interface PlotListener {
    void plotAdded(AbstractPlot plot);
    void plotRemoved(AbstractPlot plot);
    void plotsChanged();
    void plotChanged(AbstractPlot plot);
}