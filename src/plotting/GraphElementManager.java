package plotting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import parser.EvaluationContext;
import plotting.data.curve.CurveData;
import plotting.data.curve.Intersection;
import plotting.plots.AbstractPlot;
import rendering.camera.Viewport;

public class GraphElementManager{
    public ArrayList<GraphElement> elements;
    public final ArrayList<CurveData> curveCache;
    public final ArrayList<Intersection> intersectionCache;
    public PlotInteractionController interactionController;
    public ArrayList<GraphElementListener> listeners;
    private Runnable dirtyCallback;
    private GraphElement selectedGraphElement;
    private boolean isVariablesChanged = true;
    private final ExecutorService computeExecutor = Executors.newSingleThreadExecutor(
        r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
        r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

    public GraphElementManager(PlotInteractionController plotInteractionController){
        elements = new ArrayList<>();
        selectedGraphElement = null;
        curveCache = new ArrayList<>();
        interactionController = plotInteractionController;
        intersectionCache = new ArrayList<>();
        plotInteractionController.setCaches(this);
        this.listeners = new ArrayList<>();
    }
    public void addElement(GraphElement element){
        if(element instanceof Variable)isVariablesChanged = true;
        elements.add(element);
        for(GraphElementListener listener : listeners){
            listener.elementsChanged();
            listener.elementAdded(element);
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
            listener.elementsChanged();
            listener.elementRemoved(element);
        }
        markUnsaved();
    }
    public void computeCurveData(Viewport viewport){
        computeExecutor.submit(() -> {
            computeCurveDataInternal(viewport);
        });
    }

    private void computeCurveDataInternal(Viewport viewport){
        Map<String, Double> variables = buildVariableMap();

        EvaluationContext context = new EvaluationContext(variables);
        ArrayList<Intersection> newIntersectionCache = new ArrayList<>();
        for(int i = 0; i < elements.size(); i++){
            if(elements.get(i) instanceof AbstractPlot element1)
            for(int j = i + 1; j < elements.size(); j++){
                if(elements.get(j) instanceof AbstractPlot element2)
                newIntersectionCache.addAll(PlotComputationEngine.computeIntersections(element1, element2, context, viewport));
            }
        }

        ArrayList<CurveData> newCurveCache = new ArrayList<>();

        ArrayList<Future<CurveData>> futures = new ArrayList<>();
        for(GraphElement element : elements){
            if(element instanceof AbstractPlot plot)
            futures.add(pool.submit(
                () -> PlotComputationEngine.computeCurveData(plot, context, viewport)
            ));
        }
        for(Future<CurveData> future : futures){
            try {
                newCurveCache.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(() -> {
            intersectionCache.clear();
            intersectionCache.addAll(newIntersectionCache);
            curveCache.clear();
            curveCache.addAll(newCurveCache);
        });
    }

    public void removeAll(){
        elements.clear();
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

    public void close() {
        computeExecutor.shutdownNow();
        pool.shutdownNow(); 

        try {
            if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
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
        System.out.println(variables);

        return variables;
    }
}