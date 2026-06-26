package settings;

public class RendererSettings {
    public boolean showGrid;
    public boolean showAxes;
    public boolean showAxesTicks;
    public boolean showLabels;
    public boolean showLabelsOutOfView;

    public RendererSettings(){
        showAxes = true;
        showAxesTicks = true;
        showGrid = true;
        showLabels = true;
        showLabelsOutOfView = true;
    }
}
