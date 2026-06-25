package engine.UI.controls;

import engine.interaction.UndoManager;
import engine.interaction.commands.RemovePlotCommand;
import engine.plotting.PlotManager;
import engine.plotting.plots.Plot;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class PlotEditor extends VBox {
    Plot plot;
    PlotManager plotManager;
    UndoManager undoManager;
    public VBox getUI() {
        return this;
    }

    protected abstract void buildPlot()
        throws Exception;

    public void close(){    System.out.println("close");
    System.out.println(System.identityHashCode(plot));
        undoManager.execute(new RemovePlotCommand(plot, plotManager));
    }
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }
}