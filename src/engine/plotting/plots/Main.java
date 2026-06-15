package engine.plotting.plots;

import engine.rendering.Viewport;
import javafx.scene.paint.Color;

public class Main {
    public static void main(String[] args){
        ImplicitPlot plot = new ImplicitPlot("yes", (x,y) -> x*x + y*y - 25, Color.YELLOW);

        Viewport viewport = new Viewport(900, 900);
        viewport.cameraX = 0;
        viewport.cameraY = 0;

        plot.sample(viewport);
    }
}
