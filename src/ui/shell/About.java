package ui.shell;

import javafx.scene.control.Alert;

public class About extends Alert{
    public About(){
        super(AlertType.INFORMATION);

        setTitle("About");
        setHeaderText("PLACEHOLDER");
        setContentText("""
        Version 1.0

        A desktop graphing calculator developed as a personal project.

        Supports 4 plot types:
        - Function : y = f(x)
        - Parametric : x(t), y(t)
        - Polar : r = f(\u03B8)
        - Implicit f(x,y) = 0

        Designed and implemented from scratch in Java and JavaFX

        © 2026 PERSON
        """);
    }
}
