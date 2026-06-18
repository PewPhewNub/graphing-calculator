package engine.rendering;

import core.model.CameraIntent;

public class CameraSystem {
    Viewport viewport;
    public CameraSystem(Viewport viewport){
        this.viewport = viewport;
    }

    public void handle(CameraIntent intent){
        viewport.pan(intent.deltaX(), intent.deltaY());
        if(intent.zoomAtMouse()){
            viewport.zoomAt(intent.mouseX(), intent.mouseY(), intent.zoomDelta());
        }else if(intent.zoomDelta() != 0){
            viewport.zoomAt(viewport.getViewportCenterX(), viewport.getViewportCenterY(), intent.zoomDelta());
        }else{
            viewport.setScaleX((1 + intent.stretchXDelta())*(viewport.scaleX));
            viewport.setScaleY((1 + intent.stretchYDelta())*(viewport.scaleY));
        }
    }

    public void resetView(){
        viewport.cameraX = 0;
        viewport.cameraY = 0;
        viewport.setZoom(100);
        viewport.setScaleX(1);
        viewport.setScaleY(1);
    }

    public void resetAspectRatio(){
        viewport.setScaleX(1);
        viewport.setScaleY(1);
    }

    public void goTo(double x, double y){
        viewport.cameraX = x;
        viewport.cameraY = y;
    }
}
