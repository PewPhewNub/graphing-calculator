package computation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import parser.EvaluationContext;
import plotting.GraphElement;
import plotting.GraphElementListener;
import plotting.GraphElementManager;
import plotting.Variable;
import plotting.data.curve.CurveData;
import plotting.data.curve.FunctionCurveData;
import plotting.data.curve.ImplicitCurveData;
import plotting.data.curve.Intersection;
import plotting.data.curve.ParametricCurveData;
import plotting.data.curve.PolarCurveData;
import plotting.plots.AbstractPlot;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;
import rendering.camera.Viewport;

public class ComputationCoordinator implements GraphElementListener{
    private final GraphElementManager manager;
    private final Map<AbstractPlot, AbstractPlotComputer<?,?>> computers = new HashMap<>();
    private final ArrayList<Intersection> intersections = new ArrayList<>();

    public ComputationCoordinator(GraphElementManager manager){
        this.manager = manager;
    }

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

    public void compute(Viewport viewport) {
        EvaluationContext context = manager.buildEvaluationContext();

        List<Future<?>> futures = new ArrayList<>();

        for (AbstractPlotComputer<?, ?> computer : computers.values()) {
            futures.add(pool.submit(() -> {
                computer.ensureCoverage(viewport, context.copy());
                computer.generateCurveData(viewport, context.copy());
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public ArrayList<CurveData> getCurveData() {
        ArrayList<CurveData> data = new ArrayList<>();

        for(AbstractPlotComputer<?, ?> computer : computers.values()){
            data.add(computer.getData());
        }
        return data;
    }
    public ArrayList<Intersection> getIntersections() {
        return intersections;
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

    @Override
    public void elementsChanged() {
        return;
    }

    @Override
    public void elementAdded(GraphElement element) {
        if(element instanceof AbstractPlot p){
            computers.put(p, generateComputer(p));
        }
    }

    @Override
    public void elementRemoved(GraphElement element) {
        if(element instanceof AbstractPlot p){
            computers.remove(p);
        }
    }

    @Override
    public void elementChanged(GraphElement element) {
        if(element instanceof AbstractPlot plot)
            computers.get(plot).invalidate();
        if(element instanceof Variable){
            for(AbstractPlotComputer<?, ?> computer : computers.values()){
                computer.invalidate();
            }
        }
    }

    @Override
    public void selectedElementChanged(GraphElement element) {
        return;
    }

    @Override
    public void elementsSwapped(GraphElement element1, GraphElement element2) {
        return;
    }

    @Override
    public void elementsSwapped(int index1, int index2) {
        return;
    }

    @Override
    public void elementMovedTo(GraphElement element, int index) {
        return;
    }

    private AbstractPlotComputer<?, ?> generateComputer(AbstractPlot plot){
        if(plot instanceof FunctionPlot p){
            return new FunctionComputer(p, new FunctionCurveData(p));
        }
        if(plot instanceof PolarPlot p){
            return new PolarComputer(p, new PolarCurveData(p));
        }
        if(plot instanceof ParametricPlot p){
            return new ParametricComputer(p, new ParametricCurveData(p));
        }
        if(plot instanceof ImplicitPlot p){
            return new ImplicitComputer(p, new ImplicitCurveData(p));
        }
        return null;
    }
    public Map<AbstractPlot, AbstractPlotComputer<?, ?>> getComputers() {
        return computers;
    }
}
