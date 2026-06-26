package rendering.layers;

import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import plotting.data.GridData;
import rendering.core.RenderContext;

public class AxisRenderer {
    public void drawAxes(RenderContext context, Color axesColor){
        context.getGc().setStroke(axesColor);
        context.getGc().setLineWidth(2);
        context.getGc().setLineDashes(0);
        context.getGc().strokeLine(0, context.getViewport().worldToScreenY(0), context.getViewport().width, context.getViewport().worldToScreenY(0));
        context.getGc().strokeLine(context.getViewport().worldToScreenX(0), 0, context.getViewport().worldToScreenX(0), context.getViewport().height);
    }

    public void drawAxesTicks(RenderContext context, GridData data, Color axesColor){
        context.getGc().setStroke(axesColor);
        context.getGc().setLineWidth(1);
        context.getGc().setLineDashes(0);

        double gridStepX = data.stepX;
        double gridStepY = data.stepY;
        for (double i = Math.floor(context.getState().left / gridStepX); i < Math.ceil(context.getState().right / gridStepX); i++) {
            double x = i * gridStepX;
            context.getGc().strokeLine(context.getViewport().worldToScreenX(x), context.getState().axisY - 5, context.getViewport().worldToScreenX(x), context.getState().axisY + 5);
        }
        for (double i = Math.floor(context.getState().bottom / gridStepY); i < Math.ceil(context.getState().top / gridStepY); i++) {
            double y = i * gridStepY;
            context.getGc().strokeLine(context.getState().axisX - 5, context.getViewport().worldToScreenY(y), context.getState().axisX + 5, context.getViewport().worldToScreenY(y));
        }
    }

    public void drawLabels(RenderContext context, GridData data, Color labelColor, boolean drawOffScreen){
        double gridStepX = data.stepX;
        double gridStepY = data.stepY;
        context.getGc().setLineWidth(0.5);
        context.getGc().setTextAlign(TextAlignment.CENTER);
        context.getGc().setFill(labelColor);
        int exponent = (int)Math.floor(Math.log10(gridStepX));
        String formatter = "%." + -exponent + "f";

        for (double i = Math.floor(context.getState().left / gridStepX); i < Math.ceil(context.getState().right / gridStepX); i++) {
            double x = i * gridStepX;
            String value;
            if(Math.abs(x) < 1e-7) continue;
            if(exponent >= 0) value = Integer.toString((int)x); 
            else value = String.format(formatter, x);
            if(context.getState().xAxisOnTop && drawOffScreen) context.getGc().fillText(value, context.getViewport().worldToScreenX(x), context.getViewport().height - 10);
            else if(context.getState().xAxisOnBottom && drawOffScreen) context.getGc().fillText(value, context.getViewport().worldToScreenX(x), 20);
            else context.getGc().fillText(value, context.getViewport().worldToScreenX(x), context.getViewport().worldToScreenY(0) + 20);
        }
        
        context.getGc().setTextAlign(TextAlignment.RIGHT);
        exponent = (int)Math.floor(Math.log10(gridStepY));
        formatter = "%." + -exponent + "f";
        if(context.getState().yAxisOnLeft) context.getGc().setTextAlign(TextAlignment.RIGHT);
        if(context.getState().yAxisOnRight) context.getGc().setTextAlign(TextAlignment.LEFT);
        for (double i = Math.floor(context.getState().bottom / gridStepY); i < Math.ceil(context.getState().top/gridStepY); i++) {
            double y = i * gridStepY;
            String value;
            if(Math.abs(y) < 1e-7) continue;
            if(exponent >= 0) value = Integer.toString((int)y); 
            else value = String.format(formatter, y);
            if(context.getState().yAxisOnLeft && drawOffScreen)context.getGc().fillText(value, context.getViewport().width - 10, context.getViewport().worldToScreenY(y) + 5);
            else if(context.getState().yAxisOnRight && drawOffScreen) context.getGc().fillText(value, 10, context.getViewport().worldToScreenY(y) + 5);
            else context.getGc().fillText(value, context.getViewport().worldToScreenX(0) - 17, context.getViewport().worldToScreenY(y) + 5);
        }
    }
}
