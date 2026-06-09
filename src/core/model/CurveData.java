package core.model;

import java.util.ArrayList;

import engine.plotting.Plot;

public record CurveData(
    Plot originalPlot,
    ArrayList<Segment2D> visiblePoints
){}
