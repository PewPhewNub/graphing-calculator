package Simulation.Plot;

import javafx.geometry.Point2D;

public record PlotSelection(
    Plot plot,
    Point2D point,
    double distanceSquared
){}