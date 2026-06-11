package engine.rendering;

import java.util.ArrayList;

import core.model.GridData;
import core.model.Segment2D;
import core.model.ViewportState;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Renderer {
    public Viewport viewport;
    public GraphicsContext gc;
    public ViewportState state;

    double markerX; double markerY;

    Color axesColor; Color gridLinesColor; Color labelColor;
    public Renderer(Viewport viewport, GraphicsContext gc){
        this.viewport = viewport;
        this.gc = gc;
        this.axesColor = Color.BLACK;
        this.gridLinesColor = Color.BLACK;
        this.labelColor =  Color.GREY;
    }
    public void setColor(Color axesColor, Color gridColor, Color labelColor){
        this.axesColor = axesColor;
        this.gridLinesColor = gridColor;
        this.labelColor =  labelColor;
    }
    public void setState(ViewportState state) {
        this.state = state;
    }

    public void drawAxes(){
        gc.setStroke(axesColor);
        gc.setLineWidth(2);
        gc.setLineDashes(0);
        gc.strokeLine(0, viewport.worldToScreenY(0), viewport.width, viewport.worldToScreenY(0));
        gc.strokeLine(viewport.worldToScreenX(0), 0, viewport.worldToScreenX(0), viewport.height);
    }

    public void clearCanvas(){
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, viewport.width, viewport.height);
    }

    public void drawAxesTicks(GridData data){
        gc.setStroke(axesColor);
        gc.setLineWidth(1);
        gc.setLineDashes(0);

        double gridStepX = data.stepX;
        double gridStepY = data.stepY;
        for (double i = Math.floor(state.left / gridStepX); i < Math.ceil(state.right / gridStepX); i++) {
            double x = i * gridStepX;
            gc.strokeLine(viewport.worldToScreenX(x), state.axisY - 5, viewport.worldToScreenX(x), state.axisY + 5);
        }
        for (double i = Math.floor(state.bottom / gridStepY); i < Math.ceil(state.top / gridStepY); i++) {
            double y = i * gridStepY;
            gc.strokeLine(state.axisX - 5, viewport.worldToScreenY(y), state.axisX + 5, viewport.worldToScreenY(y));
        }
    }

    public void drawGridlines(GridData data){
        double gridStepX = data.stepX;
        double gridStepY = data.stepY;
        gc.setStroke(gridLinesColor);
        gc.setLineWidth(.2);
        for (double x = Math.floor(state.left / gridStepX) * gridStepX; x < state.right; x += gridStepX) {
            gc.strokeLine(viewport.worldToScreenX(x), 0, viewport.worldToScreenX(x), viewport.height);
        }
        for (double y = Math.floor(state.bottom / gridStepY)* gridStepY; y < state.top; y += gridStepY) {
            gc.strokeLine(0, viewport.worldToScreenY(y), viewport.width, viewport.worldToScreenY(y));
        }
    }

    public void drawLabels(GridData data){
        double gridStepX = data.stepX;
        double gridStepY = data.stepY;
        gc.setLineWidth(0.5);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(labelColor);
        int exponent = (int)Math.floor(Math.log10(gridStepX));
        String formatter = "%." + -exponent + "f";


        for (double i = Math.floor(state.left / gridStepX); i < Math.ceil(state.right / gridStepX); i++) {
            double x = i * gridStepX;
            String value;
            if(Math.abs(x) < 1e-7) continue;
            if(exponent >= 0) value = Integer.toString((int)x); 
            else value = String.format(formatter, x);
            if(state.xAxisOnTop) gc.fillText(value, viewport.worldToScreenX(x), viewport.height - 10);
            else if(state.xAxisOnBottom) gc.fillText(value, viewport.worldToScreenX(x), 20);
            else gc.fillText(value, viewport.worldToScreenX(x), viewport.worldToScreenY(0) + 20);
        }
        
        gc.setTextAlign(TextAlignment.RIGHT);
        exponent = (int)Math.floor(Math.log10(gridStepY));
        formatter = "%." + -exponent + "f";
        if(state.yAxisOnLeft) gc.setTextAlign(TextAlignment.RIGHT);
        if(state.yAxisOnRight) gc.setTextAlign(TextAlignment.LEFT);
        for (double i = Math.floor(state.bottom / gridStepY); i < Math.ceil(state.top/gridStepY); i++) {
            double y = i * gridStepY;
            String value;
            if(Math.abs(y) < 1e-7) continue;
            if(exponent >= 0) value = Integer.toString((int)y); 
            else value = String.format(formatter, y);
            if(state.yAxisOnLeft)gc.fillText(value, viewport.width - 10, viewport.worldToScreenY(y) + 5);
            else if(state.yAxisOnRight) gc.fillText(value, 10, viewport.worldToScreenY(y) + 5);
            else gc.fillText(value, viewport.worldToScreenX(0) - 17, viewport.worldToScreenY(y) + 5);
        }
    }
    public void drawCurve(ArrayList<Point2D> points, Color color){
        if(points.isEmpty()) return;
        gc.setStroke(color);
        gc.setLineWidth(2);
        double lastScreenX = viewport.worldToScreenX(points.get(0).getX());
        double lastScreenY = viewport.worldToScreenY(points.get(0).getY());
        gc.beginPath();
        gc.moveTo(lastScreenX, lastScreenY);
        for(int i = 1; i < points.size(); i+=1){
            Point2D point = points.get(i);
            double pointX = viewport.worldToScreenX(point.getX());
            double pointY = viewport.worldToScreenY(point.getY());
            
            if(!Double.isFinite(pointX) || !Double.isFinite(pointY)){
                gc.moveTo(pointX, pointY); 
                continue;
            }
            double dx = lastScreenX - pointX;
            double dy = lastScreenY - pointY;
            if(dx*dx + dy*dy < 1) continue; 
            lastScreenX = pointX;
            lastScreenY = pointY;
            if(point.getX() < state.left - state.marginX || point.getX() > state.right + state.marginX){
                lastScreenX = pointX;
                lastScreenY = pointY;
                gc.moveTo(lastScreenX, lastScreenY);
                continue;
            }
            if(point.getY() > state.top + state.marginY || point.getY() < state.bottom - state.marginY){
                lastScreenX = pointX;
                lastScreenY = pointY;
                gc.moveTo(lastScreenX, lastScreenY);
                continue;
            }
            gc.lineTo(lastScreenX, lastScreenY);

        }
        gc.stroke();
    }

    public void drawCurveSegmented(ArrayList<Segment2D> list, Color color) {
        if (list.isEmpty()) return;
        gc.setStroke(color);
        gc.setLineWidth(2.5);
        double limit = Math.max(gc.getCanvas().getWidth(), gc.getCanvas().getHeight()) * 10;
        for (Segment2D seg : list) {
            double x1 = viewport.worldToScreenX(seg.point1.getX());
            double y1 = viewport.worldToScreenY(seg.point1.getY());
            double x2 = viewport.worldToScreenX(seg.point2.getX());
            double y2 = viewport.worldToScreenY(seg.point2.getY());

            // Skip any residual non-finite coords (shouldn't happen after Fix 1, but be safe)
            if (!Double.isFinite(x1) || !Double.isFinite(y1) ||
                !Double.isFinite(x2) || !Double.isFinite(y2)) continue;

            // Clamp to a large-but-finite range so the OS rasteriser clips correctly
            x1 = Math.max(-limit, Math.min(limit, x1));
            y1 = Math.max(-limit, Math.min(limit, y1));
            x2 = Math.max(-limit, Math.min(limit, x2));
            y2 = Math.max(-limit, Math.min(limit, y2));

            gc.strokeLine(x1, y1, x2, y2);
        }
}
    public void drawMarker(Point2D point, double radius, Color color){
        if(point == null) return;
        gc.setFill(color);
        double screenX = viewport.worldToScreenX(point.getX());
        double screenY = viewport.worldToScreenY(point.getY());
        gc.fillOval(screenX - radius/2, screenY - radius/2, radius, radius);
    }

    public void drawInspectionLabel(Point2D point, Color color){
        double screenX = viewport.worldToScreenX(point.getX());
        double screenY = viewport.worldToScreenY(point.getY());
        String value = String.format("(%.5f, %.5f)", point.getX(), point.getY());
        int length = value.length();

        gc.setFill(Color.WHITE);
        gc.fillRoundRect(screenX + 10, screenY + 20, length * 5.8, 22, 5, 5);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5);
        gc.strokeRoundRect(screenX + 10, screenY + 20, length * 5.8, 22, 5, 5);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.GREY);
        gc.fillText(value, screenX + 10 + (length * 2.9), screenY + 35);
    }

    public void drawSlopeField(double gridStepX, double gridStepY){
        gc.setStroke(gridLinesColor);
        gc.setLineWidth(.2);
        for (double x = Math.floor(state.left / gridStepX) * gridStepX; x < state.right; x += gridStepX) {
            gc.strokeLine(viewport.worldToScreenX(x), 0, viewport.worldToScreenX(x), viewport.height);
        }
        for (double y = Math.floor(state.bottom / gridStepY)* gridStepY; y < state.top; y += gridStepY) {
            gc.strokeLine(0, viewport.worldToScreenY(y), viewport.width, viewport.worldToScreenY(y));
        }
    }

    public void drawArrowScreen(Point2D worldStart, double angle, Color color, double lengthPx) {
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(0.8);

        // --- convert base point to screen ---
        double sx = viewport.worldToScreenX(worldStart.getX());
        double sy = viewport.worldToScreenY(worldStart.getY());

        // --- direction in screen space ---
        double dx = Math.cos(angle);
        double dy = Math.sin(angle);

        // --- endpoint in screen space (FIXED LENGTH) ---
        double ex = sx + dx * lengthPx;
        double ey = sy + dy * lengthPx;

        // --- perpendicular ---
        double px = -dy;
        double py = dx;

        double headLen = lengthPx * 0.25;
        double headWid = lengthPx * 0.1;

        double bx = ex - dx * headLen;
        double by = ey - dy * headLen;

        double lx = bx + px * headWid;
        double ly = by + py * headWid;

        double rx = bx - px * headWid;
        double ry = by - py * headWid;

        // --- draw line ---
        gc.strokeLine(sx, sy, ex, ey);

        // --- draw head ---
        gc.fillPolygon(
            new double[]{ex, lx, rx},
            new double[]{ey, ly, ry},
            3
        );
    }
}
