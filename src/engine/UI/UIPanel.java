package engine.UI;
import engine.UI.controls.FunctionPlotEditor;
import engine.UI.controls.ImplicitPlotEditor;
import engine.UI.controls.ODEPlotEditor;
import engine.UI.controls.ParametricPlotEditor;
import engine.UI.controls.PolarPlotEditor;
import engine.plotting.PlotManager;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ParametricPlot;
import engine.plotting.plots.Plot;
import engine.plotting.plots.PolarPlot;
import engine.scene.GraphScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
public class UIPanel extends BorderPane {
    GraphScene graphScene;
    Button showButton;
    VBox sidePane;
    BorderPane plotPane;
    VBox plotEditorPane;
    boolean isVisible = true;
    double maxWidth;
    double minWidth;
    public MenuButton addPlotButton;
    public UIPanel(double width, double height, GraphScene graphScene){
        super();
        setHeight(height);
        setPrefWidth(width);
        this.graphScene = graphScene;
        maxWidth = width;

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
            plotEditorPane.getChildren().add(new FunctionPlotEditor(graphScene.getPlotManager()));
        });
        MenuItem odeItem = new MenuItem("ODE");
        odeItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new ODEPlotEditor(graphScene.getPlotManager()));
        });
        MenuItem polarItem = new MenuItem("Parametric");
        polarItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new ParametricPlotEditor(graphScene.getPlotManager()));
        });
        MenuItem parametricItem = new MenuItem("Polar");
        parametricItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new PolarPlotEditor(graphScene.getPlotManager()));
        });

        MenuItem implicitItem = new MenuItem("Implicit");
        implicitItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new ImplicitPlotEditor(graphScene.getPlotManager()));
        });

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
                plotEditorPane.getChildren().add(
                    new FunctionPlotEditor(
                        plotManager,
                        p
                    )
                );
            }
            if(plot instanceof ParametricPlot p) {
                plotEditorPane.getChildren().add(
                    new ParametricPlotEditor(
                        plotManager,
                        p
                    )
                );
            }
            if(plot instanceof PolarPlot p) {
                plotEditorPane.getChildren().add(
                    new PolarPlotEditor(
                        plotManager,
                        p
                    )
                );
            }
        }
    }
}
