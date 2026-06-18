package engine.rendering;

public class GraphSettings {
    public boolean showGridlines = true;
    public boolean showAxesTicks = true;
    public boolean showTickNumbering = true;

    public void setShowAxesTicks(boolean showAxesTicks) {
        this.showAxesTicks = showAxesTicks;
    }
    public void setShowGridlines(boolean showGridlines) {
        this.showGridlines = showGridlines;
    }
    public void setShowTickNumbering(boolean showTickNumbering) {
        this.showTickNumbering = showTickNumbering;
    }
}
