package ui.controls;

import interaction.commands.RemoveElementCommand;
import javafx.scene.layout.VBox;
import plotting.GraphElementManager;
import plotting.plots.AbstractPlot;

public abstract class AbstractPlotEditor extends AbstractEditor {
    AbstractPlot plot;
    GraphElementManager plotManager;

    public VBox getUI() {
        return this;
    }

    public void close(){    
        this.undoManager.execute(new RemoveElementCommand(plot, plotManager));
    }
    protected void initialize(){
        super.initialize();
    }

    protected abstract void updateElement();

    protected void attachListeners(){
        colorChooser.colorProperty().addListener((obs, oldColor, newColor) -> {
            if(updatingFields)
                return;
            updateElement();
        });

        nameLabel.textProperty().addListener((obs, oldValue, newValue) -> {
            if(updatingFields)
                return;
            updateElement();
        });

        setOnMouseClicked(e -> {
            System.out.println("Clicked " + plot.getClass().getSimpleName());
            plotManager.setSelectedElement(plot);
        });

        focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue) plotManager.setSelectedElement(null);
        });
    }
}