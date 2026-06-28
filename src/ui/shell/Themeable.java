package ui.shell;

import javafx.scene.Node;
import settings.ThemeColors;

public interface Themeable {
    public void applyTheme(Node node, ThemeColors colors);
}
