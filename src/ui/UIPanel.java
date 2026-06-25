package ui;
import java.util.HashMap;
import java.util.Map;

import interaction.UndoManager;
import interaction.commands.AddPlotCommand;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import plotting.PlotListener;
import plotting.PlotManager;
import plotting.plots.FunctionPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.Plot;
import plotting.plots.PolarPlot;
import scene.GraphScene;
import settings.ThemeColors;
import ui.controls.FunctionPlotEditor;
import ui.controls.ParametricPlotEditor;
import ui.controls.PlotEditor;
import ui.controls.PolarPlotEditor;
public class UIPanel extends BorderPane implements PlotListener, Themeable{
    GraphScene graphScene;
    PlotManager plotManager;
    Button showButton;
    VBox sidePane;
    BorderPane plotPane;
    VBox plotEditorPane;
    boolean isVisible = true;
    double maxWidth;
    double minWidth;
    public MenuButton addPlotButton;
    private Map<Plot, PlotEditor> editors;
    private UndoManager undoManager;
    public UIPanel(double width, double height, GraphScene graphScene){
        super();
        setHeight(height);
        setPrefWidth(width);
        this.graphScene = graphScene;
        maxWidth = width;
        this.editors = new HashMap<>();
        this.plotManager = graphScene.getPlotManager();

        sidePane = new VBox();
        minWidth = 56;
        sidePane.setMinSize(50, 0);
        plotPane = new BorderPane();

        showButton = new Button();
        showButton.setText(" ≡ ");
        showButton.setFont(new Font(20));
        showButton.setPadding(new Insets(1, 0, 0, 1));
        showButton.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(3),
                new Insets(2)
            )
        ));
        showButton.setAlignment(Pos.CENTER);
        showButton.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGREY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(0, 0, 2, 0)
            )
        ));
        showButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                isVisible = !isVisible;
                if(isVisible) expand(); else collapse();
            }
        });
        showButton.setPrefSize(50, 50);
        showButton.setMaxSize(50, 50);

        addPlotButton = new MenuButton(" Add Plot");

        MenuItem functionItem = new MenuItem("Function");
        functionItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddPlotCommand(
                    new FunctionPlot(),
                    this.plotManager)
            );
        });
        MenuItem odeItem = new MenuItem("ODE");
        
        MenuItem parametricItem = new MenuItem("Parametric");
        parametricItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddPlotCommand(
                    new ParametricPlot(),
                    this.plotManager)
            );
        });
        MenuItem polarItem = new MenuItem("Polar");
        polarItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddPlotCommand(
                    new PolarPlot(),
                    this.plotManager)
            );
        });

        MenuItem implicitItem = new MenuItem("Implicit");
        

        addPlotButton.getItems().addAll(
            functionItem,
            parametricItem,
            polarItem,
            odeItem,
            implicitItem
        );
        addPlotButton.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(2)
            )
        ));
        plotPane.setPadding(new Insets(5, 0, 5, 5));
        addPlotButton.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(3),
                new Insets(2)
            )
        ));

        plotPane.setTop(addPlotButton);

        plotEditorPane = new VBox();
        plotPane.setCenter(plotEditorPane);

        sidePane.getChildren().add(showButton);
        sidePane.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(0),
                new Insets(2)
            )
        ));
        sidePane.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0),
                new BorderWidths(2)
            )
        ));
        plotPane.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(0),
                new Insets(1)
            )
        ));
        plotEditorPane.setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0),
                new BorderWidths(1, 1, 1, 0)
            )
        ));
        

        setLeft(sidePane);
        setCenter(plotPane);

        setBackground(new Background(
            new BackgroundFill(
                Color.rgb(250, 250, 250),
                CornerRadii.EMPTY,
                Insets.EMPTY
            )
        ));

        StackPane arrow = (StackPane) this.addPlotButton.lookup(".arrow");
        if (arrow != null) {
            arrow.setVisible(false);
            arrow.setManaged(false); // Ensures the space is reclaimed
        }
        this.addPlotButton.setPadding(new Insets(5, 10, 5, 10));

        // Use a look-up to find the arrow node
        
    }
    public void collapse(){
        isVisible = false;

        plotPane.setVisible(false);
        plotPane.setManaged(false);

        setPrefWidth(minWidth);
        setMinWidth(minWidth);
        setMaxWidth(minWidth);
    }

    public void expand(){
        isVisible = true;

        plotPane.setVisible(true);
        plotPane.setManaged(true);

        setPrefWidth(maxWidth);
        setMinWidth(maxWidth);
        setMaxWidth(maxWidth);
    }

    public void rebuildEditors(){
        plotEditorPane.getChildren().clear();
        PlotManager plotManager = graphScene.getPlotManager();
        for(Plot plot : plotManager.plots){
            if(plot instanceof FunctionPlot p) {
                PlotEditor editor = new FunctionPlotEditor(
                        plotManager,
                        p
                    );
                plotEditorPane.getChildren().add(
                    editor
                );
                editor.setUndoManager(undoManager);
                editors.put(p, editor);
            }
            if(plot instanceof ParametricPlot p) {
                PlotEditor editor = new ParametricPlotEditor(
                        plotManager,
                        p
                    );
                plotEditorPane.getChildren().add(
                    editor
                );
                editor.setUndoManager(undoManager);
                editors.put(p, editor);
            }
            if(plot instanceof PolarPlot p) {
                PlotEditor editor = new PolarPlotEditor(
                        plotManager,
                        p
                    );
                plotEditorPane.getChildren().add(
                    editor
                );
                editor.setUndoManager(undoManager);
                editors.put(p, editor);
            }
        }
    }
    @Override
    public void plotAdded(Plot plot) {
        System.out.println(System.identityHashCode(plot));
        PlotManager plotManager = graphScene.getPlotManager();
        if(plot instanceof FunctionPlot p) {
            PlotEditor editor = new FunctionPlotEditor(
                    plotManager,
                    p
                );
            plotEditorPane.getChildren().add(
                editor
            );
            editor.setUndoManager(undoManager);
            editors.put(p, editor);
            return;
        }
        if(plot instanceof ParametricPlot p) {
            PlotEditor editor = new ParametricPlotEditor(
                    plotManager,
                    p
                );
            plotEditorPane.getChildren().add(
                editor
            );
            editor.setUndoManager(undoManager);
            editors.put(p, editor);
            return;
        }
        if(plot instanceof PolarPlot p) {
            PlotEditor editor = new PolarPlotEditor(
                    plotManager,
                    p
                );
            plotEditorPane.getChildren().add(
                editor
            );
            editor.setUndoManager(undoManager);
            editors.put(p, editor);
            return;
        }
    }
    @Override
    public void plotRemoved(Plot plot) { 
        System.out.println(System.identityHashCode(plot));
        System.out.println(editors.containsKey(plot));
        PlotEditor editor = editors.get(plot);
        if(editor != null){
            plotEditorPane.getChildren().remove(editor);
            editors.remove(plot);
        }
    }
    @Override
    public void plotsChanged() {
        
    }
    @Override
    public void plotChanged(Plot plot) {

    }

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
        for(PlotEditor editor : editors.values()){
            editor.setUndoManager(undoManager);
        }
    }
    public void addPlot(Plot plot){
        
    }
    @Override
    public void applyTheme(Node node, ThemeColors colors) {
        
    }
}
