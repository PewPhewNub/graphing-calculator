package ui.shell;

import interaction.UndoManager;
import interaction.commands.AddElementCommand;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import plotting.GraphElementManager;
import plotting.Variable;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ODEPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;
import scene.GraphMode;
import ui.components.ToolTip;

public class PlotMenuButton extends Button{
    GraphElementManager plotManager;
    UndoManager undoManager;
    ContextMenu plotMenu;
    public PlotMenuButton(GraphMode mode, GraphElementManager plotManager){
        setText("\u2795");
        setTooltip(new ToolTip("Add plot"));
        this.plotManager = plotManager;
        switch(mode){
            case FUNCTION: initializePlotButtonsFunction();
                break;
            case ODE: initializePlotButtonsODE();
                break;
            default:
                break;
        }

        setPadding(new Insets(0, 0, 0, 0));
        setFont(new Font(18));
        setMaxSize(30, 30);
        setPrefSize(30, 30);
        setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );
        setOnAction(e -> {
            plotMenu.show(this, Side.BOTTOM, 0, 0);
        });

        setVisible(true);
    }
    public void initializePlotButtonsFunction(){
        MenuItem functionItem = new MenuItem("Explicit");
        functionItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new FunctionPlot(),
                    this.plotManager)
            );
        });
        MenuItem parametricItem = new MenuItem("Parametric");
        parametricItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new ParametricPlot(),
                    this.plotManager)
            );
        });
        MenuItem polarItem = new MenuItem("Polar");
        polarItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new PolarPlot(),
                    this.plotManager)
            );
        });

        MenuItem implicitItem = new MenuItem("Implicit (EXPERIMENTAL)");
        implicitItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new ImplicitPlot(),
                    this.plotManager)
            );
        });

        MenuItem variableItem = new MenuItem("Variable (EXPERIMENTAL)");
        variableItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new Variable(),
                    this.plotManager)
            );
        });
        
        plotMenu = new ContextMenu();
        plotMenu.getItems().addAll(
            functionItem,
            parametricItem,
            polarItem,
            implicitItem,
            variableItem
        );
    }
    public void initializePlotButtonsODE(){
        MenuItem functionItem = new MenuItem("Explicit");
        functionItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new FunctionPlot(),
                    this.plotManager)
            );
        });
        MenuItem odeItem = new MenuItem("ODE");
        odeItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new ODEPlot(),
                    this.plotManager)
            );
        });
        
        MenuItem variableItem = new MenuItem("Variable (EXPERIMENTAL)");
        variableItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new Variable(),
                    this.plotManager)
            );
        });
        
        plotMenu = new ContextMenu();
        plotMenu.getItems().addAll(
            functionItem,
            odeItem,
            variableItem
        );
    }
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }
}
