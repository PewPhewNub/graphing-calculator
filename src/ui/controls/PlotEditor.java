package ui.controls;

import interaction.UndoManager;
import interaction.commands.RemovePlotCommand;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import plotting.PlotManager;
import plotting.plots.Plot;

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