package plotting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import parser.EvaluationContext;

public class GraphElementManager{
    public ArrayList<GraphElement> elements;
    public ArrayList<GraphElementListener> listeners;
    private Runnable dirtyCallback;
    private GraphElement selectedGraphElement;
    private boolean isVariablesChanged = true;

    public GraphElementManager(){
        elements = new ArrayList<>();
        selectedGraphElement = null;
        this.listeners = new ArrayList<>();
    }
    public void addElement(GraphElement element){
        if(element instanceof Variable)isVariablesChanged = true;
        elements.add(element);
        for(GraphElementListener listener : listeners){
            listener.elementAdded(element);
            listener.elementsChanged();
        }
        markUnsaved();
    }
    public void removeElement(GraphElement element){
        if(element instanceof Variable)isVariablesChanged = true;
        elements.remove(element);
        if(selectedGraphElement == element){
            setSelectedElement(null);
        }
        for(GraphElementListener listener : listeners){
            listener.elementRemoved(element);
            listener.elementsChanged();
        }
        markUnsaved();
    }

    public void removeAll(){
        ArrayList<GraphElement> old = new ArrayList<>(elements);

        elements.clear();

        for(GraphElement element : old){
            for(GraphElementListener listener : listeners){
                listener.elementRemoved(element);
            }
        }

        for(GraphElementListener listener : listeners){
            listener.elementsChanged();
        }

        setSelectedElement(null);
        markUnsaved();
    }
    public void addElement(int index, GraphElement element){
        elements.add(index, element);
        if(element instanceof Variable)isVariablesChanged = true;
        for(GraphElementListener listener : listeners){
            listener.elementsChanged();
            listener.elementAdded(element);
        }
        markUnsaved();
    }
    public void addListener(GraphElementListener listener){
        listeners.add(listener);
    }
    public void removeListener(GraphElementListener listener){
        listeners.remove(listener);
    }
    public void elementChanged(GraphElement element){
        if(element instanceof Variable)isVariablesChanged = true;
        for(GraphElementListener listener : listeners){
            listener.elementChanged(element);
            System.out.println("yes");
            listener.elementsChanged();
        }
        markUnsaved();
    }

    public void setDirtyCallback(Runnable dirtyCallback) {
        this.dirtyCallback = dirtyCallback;
    }
    void markUnsaved(){
        if(dirtyCallback!= null){
            dirtyCallback.run();
        }
    }

    public int getCount(){
        return elements.size();
    }

    public void setSelectedElement(GraphElement selectedElement) {
        this.selectedGraphElement = selectedElement;
        for(GraphElementListener listener : listeners){
            listener.selectedElementChanged(selectedElement);
        }
    }

    public GraphElement getSelectedElement() {
        return selectedGraphElement;
    }
    public int getSelectedIndex(){
        return elements.indexOf(selectedGraphElement);
    }

    public void swapElements(GraphElement element1, GraphElement element2){
        if(element1 == null || element2 == null) return;
        int index1 = elements.indexOf(element1);
        int index2 = elements.indexOf(element2);

        if(index1 == -1 || index2 == -1 || index1 == index2)
            return;
        
        Collections.swap(elements, index1, index2);
        for(GraphElementListener listener : listeners){
            listener.elementsSwapped(index1, index2);
        }
        setSelectedElement(element1);
    }

    public void movePlotTo(GraphElement element, int index){
        if(element == null) return;

        int currIndex = elements.indexOf(element);
        if(currIndex == -1 || currIndex == index) return;

        elements.remove(currIndex);
        elements.add(index, element);

        System.out.println("After move:");
        for(int i = 0; i < elements.size(); i++){
            System.out.println(i + ": " + elements.get(i));
        }

        for(GraphElementListener listener : listeners){
            listener.elementMovedTo(element, index);
        }

        setSelectedElement(element);
    }

    public Map<String, Double> buildVariableMap() {
        Map<String, Double> variables = new HashMap<>();

        for (GraphElement element : elements) {
            if (element instanceof Variable variable) {
                variables.put(variable.getName(), variable.getValue());
            }
        }
        return variables;
    }

    public EvaluationContext buildEvaluationContext(){
        Map<String, Double> variables = buildVariableMap();
        return new EvaluationContext(variables);
    }
}