package engine.rendering.layers;

import core.model.GridData;
import engine.rendering.core.RenderContext;
import javafx.scene.paint.Color;

public class GridRenderer {
    public void drawGridlines(RenderContext context, GridData data, Color gridLinesColor){
        double gridStepX = data.stepX;
        double gridStepY = data.stepY;
        context.getGc().setStroke(gridLinesColor);
        context.getGc().setLineWidth(.2);
        for (double x = Math.floor(context.getState().left / gridStepX) * gridStepX; x < context.getState().right; x += gridStepX) {
            context.getGc().strokeLine(context.getViewport().worldToScreenX(x), 0, context.getViewport().worldToScreenX(x), context.getViewport().height);
        }
        for (double y = Math.floor(context.getState().bottom / gridStepY)* gridStepY; y < context.getState().top; y += gridStepY) {
            context.getGc().strokeLine(0, context.getViewport().worldToScreenY(y), context.getViewport().width, context.getViewport().worldToScreenY(y));
        }
    }

}
