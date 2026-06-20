package engine.rendering.layers;

import java.util.ArrayList;

import core.model.Segment2D;
import engine.rendering.core.RenderContext;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class CurveRenderer {
    public void drawCurve(RenderContext context, ArrayList<Point2D> points, Color color){
        if(points.isEmpty()) return;
        context.getGc().setStroke(color);
        context.getGc().setLineWidth(2);
        double lastScreenX = context.getViewport().worldToScreenX(points.get(0).getX());
        double lastScreenY = context.getViewport().worldToScreenY(points.get(0).getY());
        context.getGc().beginPath();
        context.getGc().moveTo(lastScreenX, lastScreenY);
        for(int i = 1; i < points.size(); i+=1){
            Point2D point = points.get(i);
            double pointX = context.getViewport().worldToScreenX(point.getX());
            double pointY = context.getViewport().worldToScreenY(point.getY());
            
            if(!Double.isFinite(pointX) || !Double.isFinite(pointY)){
                context.getGc().moveTo(pointX, pointY); 
                continue;
            }
            double dx = lastScreenX - pointX;
            double dy = lastScreenY - pointY;
            if(dx*dx + dy*dy < 1 && (dx > 1)) continue; 
            lastScreenX = pointX;
            lastScreenY = pointY;
            if(point.getX() < context.getState().left - context.getState().marginX || point.getX() > context.getState().right + context.getState().marginX){
                lastScreenX = pointX;
                lastScreenY = pointY;
                context.getGc().moveTo(lastScreenX, lastScreenY);
                continue;
            }
            if(point.getY() > context.getState().top + context.getState().marginY || point.getY() < context.getState().bottom - context.getState().marginY){
                lastScreenX = pointX;
                lastScreenY = pointY;
                context.getGc().moveTo(lastScreenX, lastScreenY);
                continue;
            }
            context.getGc().lineTo(lastScreenX, lastScreenY);

        }
        context.getGc().stroke();
    }

    public void drawCurveSegmented(RenderContext context, ArrayList<Segment2D> list, Color color) {
        if (list.isEmpty()) return;
        context.getGc().setStroke(color);
        context.getGc().setLineWidth(2);
        double limit = Math.max(context.getGc().getCanvas().getWidth(), context.getGc().getCanvas().getHeight()) * 10;
        for (Segment2D seg : list) {
            double x1 = context.getViewport().worldToScreenX(seg.point1.getX());
            double y1 = context.getViewport().worldToScreenY(seg.point1.getY());
            double x2 = context.getViewport().worldToScreenX(seg.point2.getX());
            double y2 = context.getViewport().worldToScreenY(seg.point2.getY());
            double dx = x2 - x1;
            double dy = y2 - y1;
            // Skip any residual non-finite coords (shouldn't happen after Fix 1, but be safe)
            if (!Double.isFinite(x1) || !Double.isFinite(y1) ||
                !Double.isFinite(x2) || !Double.isFinite(y2)) continue;

            // Clamp to a large-but-finite range so the OS rasteriser clips correctly
            x1 = Math.max(-limit, Math.min(limit, x1));
            y1 = Math.max(-limit, Math.min(limit, y1));
            x2 = Math.max(-limit, Math.min(limit, x2));
            y2 = Math.max(-limit, Math.min(limit, y2));

//            if(dx*dx + dy*dy > 1)
                context.getGc().strokeLine(x1, y1, x2, y2);
        }
    }
}
