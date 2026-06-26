package plotting.plots;

import javafx.scene.paint.Color;

public class PlotOptions {
    public Color color;
    public boolean dashed;
    public double lineWidth;
    public boolean visible;

    public PlotOptions(){
        color = Color.RED;
        dashed = false;
        lineWidth = 2;
        visible = true;
    }

    public PlotOptions copy(){
        PlotOptions newOptions = new PlotOptions();
        newOptions.color = color;
        newOptions.dashed = dashed;
        newOptions.lineWidth = lineWidth;
        newOptions.visible = visible;
        return newOptions;
    }

    public boolean equals(PlotOptions plotOptions){
        return color.equals(plotOptions.color) &&
               dashed == plotOptions.dashed &&
               lineWidth == plotOptions.lineWidth &&
               visible == plotOptions.visible;
    }
}
