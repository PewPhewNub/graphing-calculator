package engine.plotting.settings;

import java.util.Set;

public interface PlotSettings {
    public Set<String> getDependents();
    public Set<String> getIndependents();
}
