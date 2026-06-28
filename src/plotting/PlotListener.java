package plotting;

import plotting.plots.AbstractPlot;

public interface PlotListener {
    void plotAdded(AbstractPlot plot);
    void plotRemoved(AbstractPlot plot);
    void plotsChanged();
    void plotChanged(AbstractPlot plot);
    void selectedPlotChanged(AbstractPlot plot);
    void plotReordered(AbstractPlot plot1, AbstractPlot plot2);
    void plotReordered(int index1, int index2);
}