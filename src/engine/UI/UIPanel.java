package engine.UI;

import engine.UI.plotEditor.FunctionPlotEditor;
import engine.UI.plotEditor.ODEPlotEditor;
import engine.UI.plotEditor.ParametricPlotEditor;
import engine.UI.plotEditor.PolarPlotEditor;
import engine.rendering.Graph;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
public class UIPanel extends BorderPane {
    Graph graph;
    Button showButton;
    VBox sidePane;
    BorderPane plotPane;
    VBox plotEditorPane;
    boolean isVisible = true;
    double maxWidth;
    double minWidth;
    public UIPanel(double width, double height, Graph graph){
        super();
        setHeight(height);
        setWidth(width);
        this.graph = graph;
        maxWidth = width;

        sidePane = new VBox();
        minWidth = 56;
        sidePane.setMinSize(50, height);
        sidePane.setSpacing(5);
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

        MenuButton addPlotButton = new MenuButton("Add Plot");

        MenuItem functionItem = new MenuItem("Function");
        functionItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new FunctionPlotEditor(graph.plotManager));
        });
        MenuItem odeItem = new MenuItem("ODE");
        odeItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new ODEPlotEditor(graph.plotManager));
        });
        MenuItem polarItem = new MenuItem("Parametric");
        polarItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new ParametricPlotEditor(graph.plotManager));
        });
        MenuItem parametricItem = new MenuItem("Polar");
        parametricItem.setOnAction(e ->{
            plotEditorPane.getChildren().add(new PolarPlotEditor(graph.plotManager));
        });

        addPlotButton.getItems().addAll(
            functionItem,
            parametricItem,
            polarItem,
            odeItem
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
    }
    public void setGraph(Graph graph) {
        this.graph = graph;
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
}
