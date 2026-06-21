package engine.rendering.core;

import java.util.ArrayList;

import core.model.GridData;
import core.model.Segment2D;
import core.model.curve.CurveData;
import core.model.curve.Intersection;
import engine.rendering.layers.AxisRenderer;
import engine.rendering.layers.CurveRenderer;
import engine.rendering.layers.GridRenderer;
import engine.rendering.layers.OverlayRenderer;
import engine.scene.GraphScene;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Renderer {
    private RenderContext context;
    private final CurveRenderer curveRenderer = new CurveRenderer();
    private final AxisRenderer axesRenderer = new AxisRenderer();
    private final GridRenderer gridRenderer = new GridRenderer();
    private final OverlayRenderer overlayRenderer = new OverlayRenderer();
    Color axesColor; Color gridLinesColor; Color labelColor;
    public Renderer(RenderContext context){
        this.axesColor = Color.BLACK;
        this.gridLinesColor = Color.BLACK;
        this.labelColor =  Color.GREY;
        this.context = context;
        context.getGc().setImageSmoothing(true);
    }

    public void render(GraphScene scene){
        clearCanvas();
        drawGridlines(scene.gridData());
        drawAxes(scene.gridData());
        drawLabels(scene.gridData());
        for(CurveData curve : (scene.getPlotManager()).curveCache){
            drawCurveSegmented(
                curve.visibleSegments(),
                curve.plot().getColor()
            );
            if(null == curve.featurePoints()) continue;
            for(Point2D point: curve.featurePoints()){
                drawMarker(point, 7, labelColor);    
            }  
        }
        for(Intersection intersection : scene.getPlotManager().intersectionCache){
            drawMarker(intersection.getPoint(), 7, labelColor);
        }
        Point2D selectedPoint = scene.getPlotManager().interactionController.getSelectedPoint();
        if(selectedPoint!= null) drawMarker(selectedPoint, 7, scene.getPlotManager().interactionController.getSelectedPlot().getColor());
    }

    public void setColor(Color axesColor, Color gridColor, Color labelColor){
        this.axesColor = axesColor;
        this.gridLinesColor = gridColor;
        this.labelColor =  labelColor;
    }
    public void drawAxes(GridData gridData){
        axesRenderer.drawAxes(context, axesColor);
        axesRenderer.drawAxesTicks(context, gridData, axesColor);
    }
    public void clearCanvas(){
        context.getGc().setFill(Color.WHITE);
        context.getGc().fillRect(0, 0, context.getViewport().width, context.getViewport().height);
    }
    public void drawGridlines(GridData data){
        gridRenderer.drawGridlines(context, data, gridLinesColor);
    }
    public void drawLabels(GridData data){
        axesRenderer.drawLabels(context, data, labelColor);
    }
    public void drawCurve(ArrayList<Point2D> points, Color color){
        curveRenderer.drawCurve(context, points, color);
    }
    public void drawCurveSegmented(ArrayList<Segment2D> segments, Color color){
        curveRenderer.drawCurveSegmented(context, segments, color);
    }
    public void drawMarker(Point2D point, double radius, Color color){
        overlayRenderer.drawMarker(context, point, radius, color);
    }
    public void drawInspectionLabel(Point2D point, Color color){
        overlayRenderer.drawInspectionLabel(context, point, color);
    }
    public void drawArrowScreen(Point2D worldStart, double angle, Color color, double lengthPx) {
        overlayRenderer.drawArrowScreen(context, worldStart, angle, color, lengthPx);
    }
}
