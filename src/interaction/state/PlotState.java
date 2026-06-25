package interaction.state;

import javafx.scene.paint.Color;

public abstract class PlotState {
    public final Color color;
    public final String name;

    public PlotState(String name, Color color){
        this.name = name;
        this.color = color;
    }
}
