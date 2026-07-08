package ui.shell;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import interaction.UndoManager;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import plotting.GraphElement;
import plotting.GraphElementListener;
import plotting.GraphElementManager;
import plotting.Variable;
import plotting.plots.AbstractPlot;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ODEPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;
import ui.controls.AbstractEditor;
import ui.controls.AbstractPlotEditor;
import ui.controls.FunctionPlotEditor;
import ui.controls.ImplicitPlotEditor;
import ui.controls.ODEPlotEditor;
import ui.controls.ParametricPlotEditor;
import ui.controls.PolarPlotEditor;
import ui.controls.VariableEditor;

public class EditorPane extends ScrollPane implements GraphElementListener{
    VBox root;
    GraphElementManager elementManager;
    UndoManager undoManager;
    private Map<GraphElement, AbstractEditor> editors;
        
    public EditorPane(UndoManager undoManager, GraphElementManager elementManager){
        this.undoManager = undoManager;
        this.elementManager = elementManager;
        setFocusTraversable(false);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
        setFitToWidth(true);
        setFitToHeight(false);
        setStyle("""
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-background-color : #FEFEFE;
        """);
        root = new VBox();
        this.editors = new HashMap<>();

        setContent(root);

        root.setFocusTraversable(false);   
    }

    public void rebuildEditors(){
        root.getChildren().clear();
        editors.clear();
        for(GraphElement plot : elementManager.elements){
            if(plot instanceof FunctionPlot p) {
                AbstractPlotEditor editor = new FunctionPlotEditor(
                        elementManager,
                        undoManager,
                        p
                    );
                root.getChildren().add(
                    editor
                );
                editors.put(p, editor);
            }
            if(plot instanceof ParametricPlot p) {
                AbstractPlotEditor editor = new ParametricPlotEditor(
                        elementManager,
                        undoManager,
                        p
                    );
                root.getChildren().add(
                    editor
                );
                editors.put(p, editor);
            }
            if(plot instanceof PolarPlot p) {
                AbstractPlotEditor editor = new PolarPlotEditor(
                        elementManager,
                        undoManager,
                        p
                    );
                root.getChildren().add(
                    editor
                );
                editors.put(p, editor);
            }
            
            if(plot instanceof ImplicitPlot p) {
                AbstractPlotEditor editor = new ImplicitPlotEditor(
                        elementManager,
                        undoManager,
                        p
                    );
                root.getChildren().add(
                    editor
                );
                editors.put(p, editor);
            }
            if(plot instanceof ODEPlot p) {
                AbstractPlotEditor editor = new ODEPlotEditor(
                        elementManager,
                        undoManager,
                        p
                    );
                root.getChildren().add(
                    editor
                );
                editors.put(p, editor);
            }

            if(plot instanceof Variable p) {
                AbstractEditor editor = new VariableEditor(
                        elementManager,
                        undoManager,
                        p
                    );
                root.getChildren().add(
                    editor
                );
                editors.put(p, editor);
            }
        }
    }
    @Override
    public void elementAdded(GraphElement element) {
        AbstractEditor editor = null;
        if(element instanceof FunctionPlot p) {
            editor = new FunctionPlotEditor(
                    elementManager,
                    undoManager,
                    p
                );
        }
        if(element instanceof ParametricPlot p) {
            editor = new ParametricPlotEditor(
                    elementManager,
                    undoManager,
                    p
                );
        }
        if(element instanceof PolarPlot p) {
            editor = new PolarPlotEditor(
                    elementManager,
                    undoManager,
                    p
                );
        }
        if(element instanceof ImplicitPlot p) {
            editor = new ImplicitPlotEditor(
                    elementManager,
                    undoManager,
                    p
                );
        }
        if(element instanceof ODEPlot p) {
            editor = new ODEPlotEditor(
                    elementManager,
                    undoManager,
                    p
                );
        }
        if(element instanceof Variable p) {
            editor = new VariableEditor(
                    elementManager,
                    undoManager,
                    p
                );
        }
        if(editor == null) return;
        root.getChildren().add(
            elementManager.elements.indexOf(element),
            editor
        );
        editors.put(element, editor);
        return;
    }
    @Override
    public void elementRemoved(GraphElement element) { 
        if(element == null) return;
        AbstractEditor editor = editors.get(element);
        if(editor != null){
            root.getChildren().remove(editor);
            editors.remove(element);
        }
    }

    @Override
    public void elementChanged(GraphElement element) {
        AbstractEditor editor = editors.get(element);
        editor.updateValues();
    }
    @Override
    public void selectedElementChanged(GraphElement element) {
        for(AbstractEditor editor : editors.values()){
            editor.setSelected(false);
        }
        if(element instanceof AbstractPlot plot){
            AbstractEditor editor = editors.get(plot);
            if(editor != null) editor.setSelected(true);
        }
    }

    @Override
    public void elementsSwapped(GraphElement element1, GraphElement element2) {
        if(element1 == null || element2 == null) return;
        int index1 = elementManager.elements.indexOf(element1);
        int index2 = elementManager.elements.indexOf(element2);
        Collections.swap(root.getChildren(), index1, index2);
    }
    @Override
    public void elementsSwapped(int index1, int index2) {
        rebuildEditors();
    }

    @Override
    public void elementMovedTo(GraphElement plot, int index) {
        rebuildEditors();
    }
    @Override
    public void elementsChanged() {
        return;
    }

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
        root.getChildren().forEach((editor) -> ((AbstractEditor)editor).setUndoManager(undoManager));
    }
}
