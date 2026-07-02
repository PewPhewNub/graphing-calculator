package ui.shell;
import interaction.UndoManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import javafx.scene.text.Font;
import plotting.GraphElementManager;
import scene.GraphMode;
import scene.GraphScene;
import settings.ThemeColors;
import ui.components.ToolTip;
public class UIPanel extends BorderPane implements Themeable{
    GraphScene graphScene;
    GraphElementManager plotManager;
    ControlBar controlBar;
    Button showButton;
    VBox sidePane;
    BorderPane plotPane;
    VBox plotEditorPane;
    boolean isVisible = true;
    double maxWidth;
    double minWidth;
    public EditorPane editorPane;
    public PlotMenuButton addPlotButton;
    private UndoManager undoManager;
    public UIPanel(double width, double height, GraphScene graphScene){
        super();
        setHeight(height);
        setPrefWidth(width);
        this.graphScene = graphScene;
        maxWidth = width;
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

        controlBar = new ControlBar(this.plotManager);
        GraphMode mode = graphScene.getMode();
        addPlotButton = new PlotMenuButton(mode, plotManager);
        controlBar.setPlotButton(addPlotButton);

        plotPane.setPadding(new Insets(5, 5, 5, 5));

        plotPane.setTop(controlBar);

        editorPane = new EditorPane(this.undoManager, plotManager);
        plotManager.addListener(editorPane);
        plotPane.setCenter(editorPane);
        
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

    
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
        addPlotButton.setUndoManager(undoManager);
        controlBar.setUndoManager(undoManager);
        editorPane.setUndoManager(undoManager);
    }
    @Override
    public void applyTheme(Node node, ThemeColors colors) {
        
    }
}
