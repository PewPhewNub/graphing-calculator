package plotting;

import plotting.plots.AbstractPlot;

public interface GraphElementListener {
    void elementsChanged();
    void elementAdded(GraphElement element);
    void elementRemoved(GraphElement element);
    void elementChanged(GraphElement element);
    void selectedElementChanged(GraphElement element);
    void elementsSwapped(GraphElement element1, GraphElement element2);
    void elementsSwapped(int index1, int index2);
    void elementMovedTo(GraphElement element, int index);
}