package ui.controls;

import interaction.UndoManager;
import interaction.commands.RemovePlotCommand;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import plotting.PlotManager;
import plotting.plots.AbstractPlot;
import ui.components.CloseButton;
import ui.components.ColorChooser;
import ui.components.EditableLabel;

public abstract class AbstractPlotEditor extends VBox {
    AbstractPlot plot;
    PlotManager plotManager;
    UndoManager undoManager;

    public ColorChooser colorChooser;
    public BorderPane topPanel;
    public EditableLabel nameLabel;
    protected boolean updatingFields;

    public VBox getUI() {
        return this;
    }

    public void close(){    
        this.undoManager.execute(new RemovePlotCommand(plot, plotManager));
    }

    public void setUndoManager(final UndoManager undoManager) {
        this.undoManager = undoManager;
    }
    protected void initialize(){
        setBackground(new Background(
            new BackgroundFill(
                Color.rgb(250, 250, 250),
                new CornerRadii(5),
                new Insets(2)
            )
        ));

        setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGREY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(2)
            )
        ));
        
        colorChooser = new ColorChooser(Color.RED);
        colorChooser.setAlignment(Pos.CENTER_RIGHT);

        topPanel = new BorderPane();
        getChildren().add(0, topPanel);
        topPanel.setLeft(colorChooser);

        nameLabel = new EditableLabel("New Function Plot", 12);
        topPanel.setCenter(nameLabel);

        final CloseButton button = new CloseButton();
        button.setOnMouseClicked(e -> {
            close();
        });
        topPanel.setRight(button);
    }

    public abstract void updateFields();

    protected abstract void buildPlot();

    protected void attachListeners(){
        colorChooser.colorProperty().addListener((obs, oldColor, newColor) -> {
            if(updatingFields)
                return;
            buildPlot();
        });

        nameLabel.textProperty().addListener((obs, oldValue, newValue) -> {
            if(updatingFields)
                return;
            buildPlot();
        });

        setOnMouseClicked(e -> {
            System.out.println("Clicked " + plot.getClass().getSimpleName());
            plotManager.setSelectedPlot(plot);
        });
    }

    public void setSelected(boolean selected){
        if(selected){
            setBackground(new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(5),
                    new Insets(2)
                )
            ));

            setBorder(new Border(
                new BorderStroke(
                    Color.CYAN,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(15),
                    new BorderWidths(2)
                )
            ));
        }else{
            setBackground(new Background(
                new BackgroundFill(
                    Color.rgb(250, 250, 250),
                    new CornerRadii(5),
                    new Insets(2)
                )
            ));

            setBorder(new Border(
                new BorderStroke(
                    Color.LIGHTGREY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(15),
                    new BorderWidths(2)
                )
            ));
        }
    }
}