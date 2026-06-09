package Simulation.Graphing;

import java.util.ArrayList;

import Simulation.Plot.Plot;
import javafx.geometry.Point2D;

public record CurveData(
    Plot originalPlot,
    ArrayList<Segment2D> visiblePoints
){}
