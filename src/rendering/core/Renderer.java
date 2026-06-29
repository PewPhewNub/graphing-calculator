package rendering.core;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import plotting.data.GridData;
import plotting.data.Segment2D;
import plotting.data.curve.CurveData;
import plotting.data.curve.Intersection;
import rendering.layers.AxisRenderer;
import rendering.layers.CurveRenderer;
import rendering.layers.GridRenderer;
import rendering.layers.OverlayRenderer;
import scene.GraphScene;
import settings.RendererSettings;

public class Renderer {
    private RenderContext context;
    private RendererSettings rendererSettings;
    private final CurveRenderer curveRenderer = new CurveRenderer();
    private final AxisRenderer axesRenderer = new AxisRenderer();
    private final GridRenderer gridRenderer = new GridRenderer();
    private final OverlayRenderer overlayRenderer = new OverlayRenderer();
    Color axesColor; Color gridLinesColor; Color labelColor;
    Color backgroundColor = Color.WHITE;
    public Renderer(RenderContext context, RendererSettings rendererSettings){
        this.axesColor = Color.BLACK;
        this.gridLinesColor = Color.BLACK;
        this.labelColor =  Color.GREY;
        this.context = context;
        context.getGc().setImageSmoothing(true);
        this.rendererSettings = rendererSettings;
    }

    public void render(GraphScene scene){
        clearCanvas();
        if(rendererSettings.showGrid)drawGridlines(scene.gridData());
        if(rendererSettings.showAxes)drawAxes();
        if(rendererSettings.showAxesTicks)drawAxesTicks(scene.gridData());
        if(rendererSettings.showLabels)drawLabels(scene.gridData(), rendererSettings.showLabelsOutOfView);
        for(CurveData curve : (scene.getPlotManager()).curveCache){
            if(curve == null) continue;
            double width = 2;
            if(curve.plot().equals(scene.getPlotManager().getSelectedPlot())) width = 4;
            drawCurveSegmented(
                curve.visibleSegments(),
                curve.plot().getColor(),
                width
            );
            if(null == curve.featurePoints()) continue;
            if(curve.plot().equals(scene.getPlotManager().getSelectedPlot())){
                for(Point2D point: curve.featurePoints()){
                    drawMarker(point, 7, labelColor);    
                }
            }  
        }
        for(Intersection intersection : scene.getPlotManager().intersectionCache){
            if(intersection.isOn(scene.getPlotManager().getSelectedPlot()))
            drawMarker(intersection.getPoint(), 7, labelColor);
        }
        Point2D selectedPoint = scene.getPlotManager().interactionController.getSelectedPoint();
        if(selectedPoint!= null){
            drawMarker(selectedPoint, 7, scene.getPlotManager().interactionController.getSelectedPlot().getColor());
            drawInspectionLabel(selectedPoint, axesColor);
        }
    }

    public void setColor(Color axesColor, Color gridColor, Color labelColor){
        this.axesColor = axesColor;
        this.gridLinesColor = gridColor;
        this.labelColor =  labelColor;
    }
    public void drawAxes(){
        axesRenderer.drawAxes(context, axesColor);
    }
    public void drawAxesTicks(GridData gridData){
        axesRenderer.drawAxesTicks(context, gridData, axesColor);
    }
    public void clearCanvas(){
        context.getGc().clearRect(0, 0, context.getViewport().getWidth(), context.getViewport().getHeight());
        if(!backgroundColor.equals(Color.TRANSPARENT)){
            context.getGc().setFill(backgroundColor);
            context.getGc().fillRect(0, 0, context.getViewport().getWidth(), context.getViewport().getHeight());
        }
    }
    public void drawGridlines(GridData data){
        gridRenderer.drawGridlines(context, data, gridLinesColor);
    }
    public void drawLabels(GridData data, boolean drawOffScreen){
        axesRenderer.drawLabels(context, data, labelColor, drawOffScreen);
    }
    public void drawCurve(ArrayList<Point2D> points, Color color){
        curveRenderer.drawCurve(context, points, color);
    }
    public void drawCurveSegmented(ArrayList<Segment2D> segments, Color color, double width){
        curveRenderer.drawCurveSegmented(context, segments, color, width);
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
    public void setBackgroundColor(Color clearColor) {
        this.backgroundColor = clearColor;
        clearCanvas();
    }
}
