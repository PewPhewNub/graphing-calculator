package rendering.layers;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import rendering.core.RenderContext;

public class OverlayRenderer {
    public void drawMarker(RenderContext context, Point2D point, double radius, Color color){
        if(point == null) return;
        context.getGc().setFill(color);
        double screenX = context.getViewport().worldToScreenX(point.getX());
        double screenY = context.getViewport().worldToScreenY(point.getY());
        context.getGc().fillOval(screenX - radius/2, screenY - radius/2, radius, radius);
    }

    public void drawInspectionLabel(RenderContext context, Point2D point, Color color){
        double screenX = context.getViewport().worldToScreenX(point.getX());
        double screenY = context.getViewport().worldToScreenY(point.getY());
        String value = String.format("(%.5f, %.5f)", point.getX(), point.getY());
        int length = value.length();

        context.getGc().setFill(Color.WHITE);
        context.getGc().fillRoundRect(screenX + 10, screenY + 20, length * 5.8, 22, 5, 5);
        context.getGc().setStroke(Color.BLACK);
        context.getGc().setLineWidth(0.5);
        context.getGc().strokeRoundRect(screenX + 10, screenY + 20, length * 5.8, 22, 5, 5);

        context.getGc().setTextAlign(TextAlignment.CENTER);
        context.getGc().setFill(Color.GREY);
        context.getGc().fillText(value, screenX + 10 + (length * 2.9), screenY + 35);
    }

    public void drawArrowScreen(RenderContext context, Point2D worldStart,
                            double dx, double dy,
                            Color color, double lengthPx) {

        context.getGc().setStroke(color);
        context.getGc().setFill(color);
        context.getGc().setLineWidth(0.8);

        // Convert direction to screen space
        dy = -dy;

        // Normalize
        double len = Math.hypot(dx, dy);
        if (len == 0.0) return;

        dx /= len;
        dy /= len;

        // Base point
        double sx = context.getViewport().worldToScreenX(worldStart.getX());
        double sy = context.getViewport().worldToScreenY(worldStart.getY());

        // Endpoint
        double ex = sx + dx * lengthPx;
        double ey = sy + dy * lengthPx;

        // Perpendicular
        double px = -dy;
        double py = dx;

        double headLen = lengthPx * 0.25;
        double headWid = lengthPx * 0.10;

        double bx = ex - dx * headLen;
        double by = ey - dy * headLen;

        double lx = bx + px * headWid;
        double ly = by + py * headWid;

        double rx = bx - px * headWid;
        double ry = by - py * headWid;

        // Shaft
        context.getGc().strokeLine(sx, sy, ex, ey);

        // Head
        context.getGc().fillPolygon(
            new double[]{ex, lx, rx},
            new double[]{ey, ly, ry},
            3
        );
    }
}
