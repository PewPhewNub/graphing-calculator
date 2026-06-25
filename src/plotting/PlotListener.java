package plotting;

import plotting.plots.Plot;

public interface PlotListener {
    void plotAdded(Plot plot);
    void plotRemoved(Plot plot);
    void plotsChanged();
    void plotChanged(Plot plot);
}