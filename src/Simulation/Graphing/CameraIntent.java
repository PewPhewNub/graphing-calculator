package Simulation.Graphing;

public record CameraIntent(
    double deltaX, double deltaY,
    double zoomDelta,
    double stretchXDelta, double stretchYDelta,
    boolean zoomAtMouse,
    double mouseX, double mouseY
){}
