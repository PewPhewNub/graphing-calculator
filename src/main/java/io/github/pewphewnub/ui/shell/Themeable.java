package io.github.pewphewnub.ui.shell;

import io.github.pewphewnub.settings.ThemeColors;
import javafx.scene.Node;

public interface Themeable {
    public void applyTheme(Node node, ThemeColors colors);
}
