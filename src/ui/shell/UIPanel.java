package ui.shell;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import interaction.UndoManager;
import interaction.commands.AddPlotCommand;
import interaction.commands.DuplicatePlotCommand;
import interaction.commands.PushPlotToBottomCommand;
import interaction.commands.PushPlotToTopCommand;
import interaction.commands.RemovePlotCommand;
import interaction.commands.ReorderPlotCommand;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import plotting.PlotListener;
import plotting.PlotManager;
import plotting.plots.AbstractPlot;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;
import scene.GraphScene;
import settings.ThemeColors;
import ui.controls.AbstractPlotEditor;
import ui.controls.FunctionPlotEditor;
import ui.controls.ImplicitPlotEditor;
import ui.controls.ParametricPlotEditor;
import ui.controls.PolarPlotEditor;
public class UIPanel extends BorderPane implements PlotListener, Themeable{
    GraphScene graphScene;
    PlotManager plotManager;
    HBox controlPane;
    Button showButton;
    VBox sidePane;
    BorderPane plotPane;
    VBox plotEditorPane;
    boolean isVisible = true;
    double maxWidth;
    double minWidth;
    public Button addPlotButton;
    public ContextMenu plotMenu;
    private Map<AbstractPlot, AbstractPlotEditor> editors;
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

        showButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(0, 1, 1, 0, false),
                    new BorderWidths(0, 0, 2, 0)
                )
            )
        );
        
        showButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                isVisible = !isVisible;
                if(isVisible) expand(); else collapse();
            }
        });
        showButton.setPrefSize(50, 50);
        showButton.setMaxSize(50, 50);

        controlPane = new HBox();

        initializePlotButtons();
        initializeControlButtons();

        plotPane.setPadding(new Insets(5, 0, 5, 0));

        controlPane.setPadding(new Insets(0));
        controlPane.setMinHeight(45);
        controlPane.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(0, 0, 2, 0)
                )
            )
        );

        plotPane.setTop(controlPane);

        plotEditorPane = new VBox();
        plotEditorPane.setPadding(new Insets(5, 0, 0, 0));
        plotPane.setCenter(plotEditorPane);
        
        plotPane.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY, 
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(0, 0, 0, 2)
                )
            )
        );

        sidePane.getChildren().add(showButton);
        sidePane.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(0),
                new Insets(0)
            )
        ));
        plotPane.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(0),
                new Insets(1)
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
        setBorder(new Border(
            new BorderStroke(
                Color.LIGHTGREY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(2, 2, 2, 2)
            )
        ));
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
        editors.clear();
        PlotManager plotManager = graphScene.getPlotManager();
        for(AbstractPlot plot : plotManager.plots){
            if(plot instanceof FunctionPlot p) {
                AbstractPlotEditor editor = new FunctionPlotEditor(
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
                AbstractPlotEditor editor = new ParametricPlotEditor(
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
                AbstractPlotEditor editor = new PolarPlotEditor(
                        plotManager,
                        p
                    );
                plotEditorPane.getChildren().add(
                    editor
                );
                editor.setUndoManager(undoManager);
                editors.put(p, editor);
            }
            
            if(plot instanceof ImplicitPlot p) {
                AbstractPlotEditor editor = new ImplicitPlotEditor(
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
    public void plotAdded(AbstractPlot plot) {
        PlotManager plotManager = graphScene.getPlotManager();
        AbstractPlotEditor editor = null;
        if(plot instanceof FunctionPlot p) {
            editor = new FunctionPlotEditor(
                    plotManager,
                    p
                );
        }
        if(plot instanceof ParametricPlot p) {
            editor = new ParametricPlotEditor(
                    plotManager,
                    p
                );
        }
        if(plot instanceof PolarPlot p) {
            editor = new PolarPlotEditor(
                    plotManager,
                    p
                );
        }
        if(plot instanceof ImplicitPlot p) {
            editor = new ImplicitPlotEditor(
                    plotManager,
                    p
                );
        }
        if(editor == null) return;
        plotEditorPane.getChildren().add(
            plotManager.plots.indexOf(plot),
            editor
        );
        editor.setUndoManager(undoManager);
        editors.put(plot, editor);
        return;
    }
    @Override
    public void plotRemoved(AbstractPlot plot) { 
        if(plot == null) return;
        AbstractPlotEditor editor = editors.get(plot);
        if(editor != null){
            plotEditorPane.getChildren().remove(editor);
            editors.remove(plot);
        }
    }
    @Override
    public void plotsChanged() {
    }
    @Override
    public void plotChanged(AbstractPlot plot) {
        AbstractPlotEditor editor = editors.get(plot);
        editor.updateFields();
    }

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
        for(AbstractPlotEditor editor : editors.values()){
            editor.setUndoManager(undoManager);
            
        }
    }
    @Override
    public void applyTheme(Node node, ThemeColors colors) {
        
    }
    @Override
    public void selectedPlotChanged(AbstractPlot plot) {
        for(AbstractPlotEditor editor : editors.values()){
            editor.setSelected(false);
        }
        if(plot == null) return;
        AbstractPlotEditor editor = editors.get(plot);
        if(editor == null) return;
        editor.setSelected(true);
    }

    @Override
    public void plotReordered(AbstractPlot plot1, AbstractPlot plot2) {
        if(plot1 == null || plot2 == null) return;
        int index1 = plotManager.plots.indexOf(plot1);
        int index2 = plotManager.plots.indexOf(plot2);
        Collections.swap(plotEditorPane.getChildren(), index1, index2);
    }
    @Override
    public void plotReordered(int index1, int index2) {
        rebuildEditors();
    }

    public void initializePlotButtons(){
        addPlotButton = new Button("\u2795");

        MenuItem functionItem = new MenuItem("Explicit");
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

        MenuItem implicitItem = new MenuItem("Implicit (EXPERIMENTAL)");
        implicitItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddPlotCommand(
                    new ImplicitPlot(),
                    this.plotManager)
            );
        });
        
        plotMenu = new ContextMenu();
        plotMenu.getItems().addAll(
            functionItem,
            parametricItem,
            polarItem,
            implicitItem
        );
        addPlotButton.setPadding(new Insets(0, 0, 0, 0));
        addPlotButton.setFont(new Font(18));
        addPlotButton.setMaxSize(30, 30);
        addPlotButton.setPrefSize(30, 30);
        addPlotButton.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        addPlotButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );
        addPlotButton.setOnAction(e -> {
            plotMenu.show(addPlotButton, Side.BOTTOM, 0, 0);
        });
        controlPane.getChildren().add(addPlotButton);

        addPlotButton.setVisible(true);
    }

    void initializeControlButtons(){
        Button moveUpButton = new Button("\u2191");
        moveUpButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index <= 0) return;

            AbstractPlot current = plotManager.plots.get(index);
            AbstractPlot above = plotManager.plots.get(index - 1);

            undoManager.execute(
                new ReorderPlotCommand(current, above, plotManager)
            );
        });

        moveUpButton.setPadding(new Insets(0, 0, 0, 0));
        moveUpButton.setFont(new Font(18));
        moveUpButton.setMaxSize(30, 30);
        moveUpButton.setPrefSize(30, 30);
        moveUpButton.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        moveUpButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );

        Button moveDownButton = new Button("\u2193");
        moveDownButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index >= plotManager.getCount() - 1) return;

            AbstractPlot current = plotManager.plots.get(index);
            AbstractPlot above = plotManager.plots.get(index + 1);

            undoManager.execute(
                new ReorderPlotCommand(current, above, plotManager)
            );
        });

        moveDownButton.setPadding(new Insets(0, 0, 0, 0));
        moveDownButton.setFont(new Font(18));
        moveDownButton.setMaxSize(30, 30);
        moveDownButton.setPrefSize(30, 30);
        moveDownButton.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        moveDownButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );

        Button moveTopButton = new Button("\u21A5");
        moveTopButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index <= 0) return;

            AbstractPlot current = plotManager.plots.get(index);

            undoManager.execute(
                new PushPlotToTopCommand(current, plotManager)
            );
        });

        moveTopButton.setPadding(new Insets(0, 0, 0, 0));
        moveTopButton.setFont(new Font(18));
        moveTopButton.setMaxSize(30, 30);
        moveTopButton.setPrefSize(30, 30);
        moveTopButton.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        moveTopButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );

        Button moveBottomButton = new Button("\u21A7");
        moveBottomButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index >= plotManager.getCount() - 1) return;

            AbstractPlot current = plotManager.plots.get(index);

            undoManager.execute(
                new PushPlotToBottomCommand(current, plotManager)
            );
        });

        moveBottomButton.setPadding(new Insets(0, 0, 0, 0));
        moveBottomButton.setFont(new Font(18));
        moveBottomButton.setMaxSize(30, 30);
        moveBottomButton.setPrefSize(30, 30);
        moveBottomButton.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        moveBottomButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );

        Button duplicateButton = new Button("\u2398");
        duplicateButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index < 0) return;
            AbstractPlot current = plotManager.plots.get(index);
            undoManager.execute(
                new DuplicatePlotCommand(current, plotManager)
            );
        });

        duplicateButton.setPadding(new Insets(0, 0, 0, 0));
        duplicateButton.setFont(new Font(18));
        duplicateButton.setMaxSize(30, 30);
        duplicateButton.setPrefSize(30, 30);
        duplicateButton.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        duplicateButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );

        Button uhSpaceTaker = new Button("\u1234");

        uhSpaceTaker.setPadding(new Insets(0, 0, 0, 0));
        uhSpaceTaker.setFont(new Font(18));
        uhSpaceTaker.setMaxSize(30, 30);
        uhSpaceTaker.setPrefSize(30, 30);
        uhSpaceTaker.setVisible(false);
        uhSpaceTaker.setManaged(true);

        Button closeButton = new Button("\u274c");
        closeButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index < 0) return;
            AbstractPlot current = plotManager.plots.get(index);
            undoManager.execute(
                new RemovePlotCommand(current, plotManager)
            );
        });

        closeButton.setPadding(new Insets(0, 0, 0, 0));
        closeButton.setFont(new Font(18));
        closeButton.setMaxSize(30, 30);
        closeButton.setPrefSize(30, 30);
        closeButton.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(2),
                    new Insets(2)
                )
            )
        );
        closeButton.setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(2),
                    new BorderWidths(2)
                )
            )
        );
        controlPane.setSpacing(10);
        controlPane.setAlignment(Pos.CENTER);
        controlPane.getChildren().add(duplicateButton);
        controlPane.getChildren().add(moveTopButton);
        controlPane.getChildren().add(moveUpButton);
        controlPane.getChildren().add(moveDownButton);
        controlPane.getChildren().add(moveBottomButton);
        controlPane.getChildren().add(uhSpaceTaker);
        controlPane.getChildren().add(closeButton);
    }

    @Override
    public void plotMovedTo(AbstractPlot plot, int index) {
        rebuildEditors();
    }
}
