package engine.UI.plotEditor;

import engine.plotting.PlotManager;
import engine.plotting.plots.Plot;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class PlotEditor extends VBox {
    Plot plot;
    PlotManager plotManager;
    public VBox getUI() {
        return this;
    }

    public abstract void buildPlot()
        throws Exception;

    public void close(){
        setManaged(false);
        setVisible(false);

        Parent parent = getParent();

        if(parent instanceof Pane pane){
            pane.getChildren().remove(this);
        }        
        if(plot != null) plotManager.removePlot(plot);
    }
}