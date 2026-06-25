package settings;

import javafx.scene.paint.Color;

public class ThemeColors {
    public Color background;
    public Color panel;
    public Color border;
    public Color text;

    public ThemeColors(
            Color background,
            Color panel,
            Color border,
            Color text) {

        this.background = background;
        this.panel = panel;
        this.border = border;
        this.text = text;
    }
}
