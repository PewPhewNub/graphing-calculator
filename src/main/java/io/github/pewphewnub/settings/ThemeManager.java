package io.github.pewphewnub.settings;

import io.github.pewphewnub.app.WindowManager;
import io.github.pewphewnub.ui.shell.MainWindow;
import javafx.scene.Scene;

public final class ThemeManager {
    private static final String CSS_PATH =
        "/io/github/pewphewnub/ui/shell/css/styles/";

    private static final String BASE =
        ThemeManager.class.getResource(
            CSS_PATH + "base.css"
        ).toExternalForm();

    private static Theme currentTheme = Theme.LIGHT;
    private static WindowManager windowManager;

    public static void setWindowManager(WindowManager windowManager) {
        ThemeManager.windowManager = windowManager;
    }
    public static void applyTheme(Theme theme) {
        for(MainWindow i : windowManager.getWindows()){
            Scene scene = i.getScene();
            for(Theme j : Theme.values()) scene.getStylesheets().remove((j.value));

            scene.getStylesheets().addAll(
                theme.getValue(), BASE
            );
        }
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }
}