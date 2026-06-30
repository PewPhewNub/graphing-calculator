package ui.shell;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import interaction.UndoManager;
import interaction.commands.AddElementCommand;
import interaction.commands.DuplicateElementCommand;
import interaction.commands.PushElementToBottomCommand;
import interaction.commands.PushElementToTopCommand;
import interaction.commands.RemoveElementCommand;
import interaction.commands.SwapElementsCommand;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import plotting.GraphElement;
import plotting.GraphElementListener;
import plotting.GraphElementManager;
import plotting.Variable;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;
import scene.GraphScene;
import settings.ThemeColors;
import ui.components.ToolTip;
import ui.controls.AbstractEditor;
import ui.controls.AbstractPlotEditor;
import ui.controls.FunctionPlotEditor;
import ui.controls.ImplicitPlotEditor;
import ui.controls.ParametricPlotEditor;
import ui.controls.PolarPlotEditor;
import ui.controls.VariableEditor;
public class UIPanel extends BorderPane implements GraphElementListener, Themeable{
    GraphScene graphScene;
    GraphElementManager plotManager;
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
    private Map<GraphElement, AbstractEditor> editors;
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
        ToolTip showTip = new ToolTip("Show Sidebar");
        ToolTip hideTip = new ToolTip("Hide Sidebar");
        
        showButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                isVisible = !isVisible;
                if(isVisible) expand(); else collapse();
            }
        });
        showButton.setTooltip(hideTip);
        showButton.setOnMouseEntered(e -> {
            if(isVisible) showButton.setTooltip(hideTip);
            else showButton.setTooltip(showTip);
        });
        showButton.setPrefSize(50, 50);
        showButton.setMaxSize(50, 50);

        controlPane = new HBox();

        initializePlotButtons();
        initializeControlButtons();

        plotPane.setPadding(new Insets(5, 5, 5, 5));

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
        plotEditorPane.setFocusTraversable(false);
        ScrollPane yes = new ScrollPane(plotEditorPane);
        yes.setFocusTraversable(false);
        yes.setHbarPolicy(ScrollBarPolicy.NEVER);
        yes.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        yes.setFitToWidth(true);
        yes.setFitToHeight(false);
        yes.setStyle("""
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-background-color : #FEFEFE;
        """);
        plotPane.setCenter(yes);
        
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
        GraphElementManager plotManager = graphScene.getPlotManager();
        for(GraphElement plot : plotManager.elements){
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
            
            if(plot instanceof Variable p) {
                AbstractEditor editor = new VariableEditor(
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
    public void elementAdded(GraphElement element) {
        GraphElementManager elementManager = graphScene.getPlotManager();
        AbstractEditor editor = null;
        if(element instanceof FunctionPlot p) {
            editor = new FunctionPlotEditor(
                    elementManager,
                    p
                );
        }
        if(element instanceof ParametricPlot p) {
            editor = new ParametricPlotEditor(
                    elementManager,
                    p
                );
        }
        if(element instanceof PolarPlot p) {
            editor = new PolarPlotEditor(
                    elementManager,
                    p
                );
        }
        if(element instanceof ImplicitPlot p) {
            editor = new ImplicitPlotEditor(
                    elementManager,
                    p
                );
        }
        if(element instanceof Variable p) {
            editor = new VariableEditor(
                    elementManager,
                    p
                );
        }
        if(editor == null) return;
        plotEditorPane.getChildren().add(
            elementManager.elements.indexOf(element),
            editor
        );
        editor.setUndoManager(undoManager);
        editors.put(element, editor);
        return;
    }
    @Override
    public void elementRemoved(GraphElement element) { 
        if(element == null) return;
        AbstractEditor editor = editors.get(element);
        if(editor != null){
            plotEditorPane.getChildren().remove(editor);
            editors.remove(element);
        }
    }

    @Override
    public void elementChanged(GraphElement element) {
        AbstractEditor editor = editors.get(element);
        editor.updateValues();
    }

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
        for(AbstractEditor editor : editors.values()){
            editor.setUndoManager(undoManager);
        }
    }
    @Override
    public void applyTheme(Node node, ThemeColors colors) {
        
    }
    @Override
    public void selectedElementChanged(GraphElement element) {
        for(AbstractEditor editor : editors.values()){
            editor.setSelected(false);
        }
        if(element == null){
            for(Node i : controlPane.getChildren()){
                ((Button)i).setDisable(true);
            }
            return;
        }
        for(Node i : controlPane.getChildren()){
            ((Button)i).setDisable(false);
        }
        AbstractEditor editor = editors.get(element);
        if(editor == null) return;
        editor.setSelected(true);
    }

    @Override
    public void elementsSwapped(GraphElement element1, GraphElement element2) {
        if(element1 == null || element2 == null) return;
        int index1 = plotManager.elements.indexOf(element1);
        int index2 = plotManager.elements.indexOf(element2);
        Collections.swap(plotEditorPane.getChildren(), index1, index2);
    }
    @Override
    public void elementsSwapped(int index1, int index2) {
        rebuildEditors();
    }

    public void initializePlotButtons(){
        addPlotButton = new Button("\u2795");
        addPlotButton.setTooltip(new ToolTip("Add plot"));

        MenuItem functionItem = new MenuItem("Explicit");
        functionItem.setOnAction(e ->{
            this.undoManager.execute(
                new AddElementCommand(
                    new FunctionPlot(),
                    this.plotManager)
            );
        });
        MenuItem odeItem = new MenuItem("ODE");
        
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
        moveUpButton.setTooltip(new ToolTip("Move selected up"));
        moveUpButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index <= 0) return;

            GraphElement current = plotManager.elements.get(index);
            GraphElement above = plotManager.elements.get(index - 1);

            undoManager.execute(
                new SwapElementsCommand(current, above, plotManager)
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
        moveDownButton.setTooltip(new ToolTip("Move selected down"));
        moveDownButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index >= plotManager.getCount() - 1) return;

            GraphElement current = plotManager.elements.get(index);
            GraphElement above = plotManager.elements.get(index + 1);

            undoManager.execute(
                new SwapElementsCommand(current, above, plotManager)
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
        moveTopButton.setTooltip(new ToolTip("Send selected to top"));
        moveTopButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index <= 0) return;

            GraphElement current = plotManager.elements.get(index);

            undoManager.execute(
                new PushElementToTopCommand(current, plotManager)
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
        moveBottomButton.setTooltip(new ToolTip("Send selected to bottom"));
        moveBottomButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index >= plotManager.getCount() - 1) return;

            GraphElement current = plotManager.elements.get(index);

            undoManager.execute(
                new PushElementToBottomCommand(current, plotManager)
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
        duplicateButton.setTooltip(new ToolTip("Duplicate selected"));
        duplicateButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index < 0) return;
            GraphElement current = plotManager.elements.get(index);
            undoManager.execute(
                new DuplicateElementCommand(current, plotManager)
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
        closeButton.setTooltip(new ToolTip("Close selected"));
        closeButton.setOnAction(e -> {
            int index = plotManager.getSelectedIndex();
            if (index < 0) return;
            GraphElement current = plotManager.elements.get(index);
            undoManager.execute(
                new RemoveElementCommand(current, plotManager)
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
    public void elementMovedTo(GraphElement plot, int index) {
        rebuildEditors();
    }
    @Override
    public void elementsChanged() {
        return;
    }
}
