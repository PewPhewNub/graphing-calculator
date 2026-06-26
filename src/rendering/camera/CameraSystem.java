package rendering.camera;

public class CameraSystem {
    Viewport viewport;
    boolean smoothMovements = true;

    double targetCameraX;
    double targetCameraY;
    double targetZoom;
    double targetScaleX;
    double targetScaleY;
    double lastTime;
    
    public CameraSystem(Viewport viewport){
        this.viewport = viewport;
        smoothMovements = true;

        targetCameraX = viewport.cameraX;
        targetCameraY = viewport.cameraY;

        targetZoom = viewport.getZoom();

        targetScaleX = viewport.scaleX;
        targetScaleY = viewport.scaleY;
        lastTime = System.nanoTime();
    }

    public void handle(CameraIntent intent){
        targetCameraX -= intent.deltaX();
        targetCameraY -= intent.deltaY();
        if(intent.zoomAtMouse()){
            double worldBeforeX =
                viewport.screenToWorldX(intent.mouseX());

            double worldBeforeY =
                viewport.screenToWorldY(intent.mouseY());
            double newTargetZoom = targetZoom * (1 + intent.zoomDelta());
            double worldAfterX =
                viewport.screenToWorldX(
                    intent.mouseX(),
                    targetCameraX,
                    newTargetZoom
                );

            double worldAfterY =
                viewport.screenToWorldY(
                    intent.mouseY(),
                    targetCameraY,
                    newTargetZoom
                );
            targetCameraX += worldBeforeX - worldAfterX;
            targetCameraY += worldBeforeY - worldAfterY;

            targetZoom = newTargetZoom;

        }else if(intent.zoomDelta() != 0){
            targetZoom *= (1 + intent.zoomDelta());
        }else{
            targetScaleX *= (1 + intent.stretchXDelta());
            targetScaleY *= (1 + intent.stretchYDelta());
        }
    }

    public void setSmoothMovement(boolean enabled){
        smoothMovements = enabled;

        if(!enabled){
            targetCameraX = viewport.cameraX;
            targetCameraY = viewport.cameraY;
            targetZoom = viewport.getZoom();
        }
    }
    public void resetView(){
        targetCameraX = 0;
        targetCameraY = 0;
        targetScaleX = 1;
        targetScaleY = 1;
        targetZoom = 90;
    }

    public void resetAspectRatio(){
        targetScaleX = 01;
        targetScaleY = 01;
    }

    public void goTo(double x, double y){
        targetCameraX = x;
        targetCameraY = y;
    }

    public void update(){
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;

        double factor = 1 - Math.exp(-60 * dt);
        if(!smoothMovements){
            viewport.cameraX = targetCameraX;
            viewport.cameraY = targetCameraY;
            viewport.setZoom(targetZoom);

            viewport.setScaleX(targetScaleX);
            viewport.setScaleY(targetScaleY);
            return;
        }
        viewport.cameraX +=
            (targetCameraX - viewport.cameraX) * .87;

        viewport.cameraY +=
            (targetCameraY - viewport.cameraY) * .87;

        double currentLog = Math.log(viewport.getZoom());
        double targetLog = Math.log(targetZoom);

        currentLog += (targetLog - currentLog) * .3;

        viewport.setZoom(Math.exp(currentLog));

        viewport.setScaleX(
            viewport.scaleX +
            (targetScaleX - viewport.scaleX) * 0.15
        );

        viewport.setScaleY(
            viewport.scaleY +
            (targetScaleY - viewport.scaleY) * 0.15
        );

        if(Math.abs(targetCameraX - viewport.cameraX) < 0.001 &&
        Math.abs(targetCameraY - viewport.cameraY) < 0.001){
            
            viewport.cameraX = targetCameraX;
            viewport.cameraY = targetCameraY;
        }

    }
}
