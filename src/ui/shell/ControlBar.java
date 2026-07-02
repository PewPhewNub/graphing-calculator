package ui.shell;

import interaction.UndoManager;
import interaction.commands.DuplicateElementCommand;
import interaction.commands.PushElementToBottomCommand;
import interaction.commands.PushElementToTopCommand;
import interaction.commands.RemoveElementCommand;
import interaction.commands.SwapElementsCommand;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import plotting.GraphElement;
import plotting.GraphElementManager;
import ui.components.ToolTip;

public class ControlBar extends HBox{
    GraphElementManager plotManager;
    UndoManager undoManager;
    public ControlBar(GraphElementManager plotManager){
        this.plotManager = plotManager;

        initializeControlButtons();

        setPadding(new Insets(0));
        setMinHeight(45);
        setBorder(
            new Border(
                new BorderStroke(
                    Color.LIGHTGRAY,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(0, 0, 2, 0)
                )
            )
        );
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
        setSpacing(10);
        setAlignment(Pos.CENTER);
        getChildren().add(duplicateButton);
        getChildren().add(moveTopButton);
        getChildren().add(moveUpButton);
        getChildren().add(moveDownButton);
        getChildren().add(moveBottomButton);
        getChildren().add(uhSpaceTaker);
        getChildren().add(closeButton);
    }
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    public void setPlotButton(PlotMenuButton button){
        getChildren().add(0, button);
    }
}
