package Simulation.Plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.BiFunction;

import Simulation.Graphing.GridData;
import Simulation.Graphing.Renderer;
import Simulation.Graphing.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import math.Core.ODESolution;
import math.Core.ODEStatus;
import math.Core.Point;
import math.ODESolvers.RungeKuttaMethod;

public class ODEPlot implements Plot {

    public BiFunction<Double, Double, Double> equation;
    ODEStatus statusLeft;
    ODEStatus statusRight;
    public Point initial;
    ArrayList<Point2D> leftBranch;
    ArrayList<Point2D> rightBranch;
    String name;
    Color color;
    public ODEPlot(String name, BiFunction<Double, Double, Double> equation, Point initial, Color color){
        long t = System.currentTimeMillis();
        System.out.println("ODE constructor start");

        this.equation = equation;
        this.initial = initial;
        this.name = name;
        leftBranch = new ArrayList<Point2D>();
        rightBranch = new ArrayList<Point2D>();
        ODESolution solutionRight = RungeKuttaMethod.adaptiveRK4(equation, initial, 1e-1, 256, 1e-7);
        ODESolution solutionLeft = RungeKuttaMethod.adaptiveRK4(equation, initial, -1e-1, -256, 1e-7);
        statusLeft = solutionLeft.status();
        statusRight = solutionRight.status();
        if(statusLeft == ODEStatus.SUCCESS){
            for(Point i : solutionLeft.list()) leftBranch.addFirst(new Point2D(i.x, i.y));
        }
        if(statusRight == ODEStatus.SUCCESS){
            for(Point i : solutionRight.list()) rightBranch.addLast(new Point2D(i.x, i.y));
        }

        this.color = color;
        
        System.out.println("ODE constructor end " + (System.currentTimeMillis() - t));
    }
    public void extendLeft(){
        if(statusLeft != ODEStatus.SUCCESS) return;
        long t = System.currentTimeMillis();
        Point2D currentEndPoint = leftBranch.getFirst();
        ODESolution solutionLeft = RungeKuttaMethod.adaptiveRK4(equation, new Point(currentEndPoint.getX(), currentEndPoint.getY()), -1e-3, currentEndPoint.getX() - 64, 1e-7);
        statusLeft = solutionLeft.status();
        ArrayList<Point> solutionList = solutionLeft.list(); 
        if(statusLeft == ODEStatus.SUCCESS){
            for(int i = 1; i < solutionList.size(); i++){
                leftBranch.addFirst(new Point2D(solutionList.get(i).x, solutionList.get(i).y));
            } 
        }
        //System.out.println(solutionLeft);
        System.out.println(
            "extendLeft took " +
            (System.currentTimeMillis() - t) +
            " ms"
        );
    }
    
    public void extendRight(){
        if(statusRight != ODEStatus.SUCCESS) return;
        long t = System.currentTimeMillis();

        Point2D currentEndPoint = rightBranch.getLast();
        ODESolution solutionRight = RungeKuttaMethod.adaptiveRK4(equation, new Point(currentEndPoint.getX(), currentEndPoint.getY()), 1e-3, currentEndPoint.getX() + 64, 1e-7);
        statusRight = solutionRight.status();
        ArrayList<Point> solutionList = solutionRight.list(); 
        if(statusRight == ODEStatus.SUCCESS){
            for(int i = 1; i < solutionList.size(); i++){
                rightBranch.addLast(new Point2D(solutionList.get(i).x, solutionList.get(i).y));
            } 
        }
        
        //System.out.println(solutionRight);
        System.out.println(
            "extendRight took " +
            (System.currentTimeMillis() - t) +
            " ms"
        );
    }

    public void drawSlopeField(Renderer r, GridData data) {
        double pixelLength = 20; // fixed screen length
        for (Point2D p : data.points) {

            double slope = -equation.apply(p.getX(), p.getY());
            if (!Double.isFinite(slope)) continue;

            double angle = Math.atan(slope);

            r.drawArrowScreen(p, angle, color, pixelLength);
        }
    }
    
    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }

    public Point2D nearestPoint(double worldX, double worldY, Viewport viewport) {//TODO:remove
        ArrayList<Point2D> data = list();
        data.addAll(leftBranch);
        data.add(new Point2D(initial.x, initial.y));
        data.addAll(rightBranch);
        if(data.isEmpty()) return null;
        if (data == null || data.isEmpty()) return null;

        // 1. Use binary search to find the index where worldX would fit
        int index = Collections.binarySearch(data, new Point2D(worldX, 0), 
                    Comparator.comparingDouble(Point2D::getX));
        
        if (index < 0) index = -(index + 1);

        // 2. Search outward from that index until the distance starts increasing
        double minD2 = Double.POSITIVE_INFINITY;
        Point2D nearest = null;

        // Check left and right
        for (int i = index; i >= 0; i--) {
            Point2D p = data.get(i);
            double d2 = Math.abs(p.getX() - worldX);
            if (d2 < minD2) {
                minD2 = d2;
                nearest = p;
            } else break; // We passed the closest point on this side
        }

        for (int i = index + 1; i < data.size(); i++) {
            Point2D p = data.get(i);
            double d2 = Math.abs(p.getX() - worldX);
            if (d2 < minD2) {
                minD2 = d2;
                nearest = p;
            } else break; // We passed the closest point on this side
        }

        return nearest;
    }

    public double distanceSquaredFrom(double x0, double y0, Viewport viewport) {//TODO: remove
        ArrayList<Point2D> data = list();
        
        if(data.isEmpty()) return Double.NaN;

        double minDist2 = Double.POSITIVE_INFINITY;

        double sx = viewport.worldToScreenX(x0);
        double sy = viewport.worldToScreenY(y0);

        for (Point2D p : data) {

            double px = viewport.worldToScreenX(p.getX());
            double py = viewport.worldToScreenY(p.getY());

            double dx = px - sx;
            double dy = py - sy;

            double dist2 = dx * dx + dy * dy;

            if (dist2 <= minDist2) {
                minDist2 = dist2;
            }
        }

        return minDist2;
    }

    public void extendCoverage(double worldMinX, double worldMaxX, double marginX){
        if(leftBranch.getFirst().getX() > (worldMinX + marginX)){
            extendLeft();
        }
        
        if(rightBranch.getLast().getX() < (worldMaxX - marginX)){
            extendRight();
        }
    }
    
    public ArrayList<Point2D> solve(Point2D initial, double stepSize, double maxSteps){
        ArrayList<Point> list = RungeKuttaMethod.adaptiveRK4(equation, new Point(initial.getX(), initial.getY()), stepSize, maxSteps, 1e-7).list();
        ArrayList<Point2D> newList = new ArrayList<>();
        for(Point i : list) newList.add(new Point2D(i.x, i.y));
        return newList;
    }

    public ArrayList<Point2D> list(){
        ArrayList<Point2D> data = new ArrayList<>();
        data.addAll(leftBranch);
        data.add(new Point2D(initial.x, initial.y));
        data.addAll(rightBranch);
        return data;
    }

    public boolean contains(Point2D point){
        double pointX = point.getX();
        double pointY = point.getY();
        for(Point2D currentPoint : list()){
            if((currentPoint.getX() - pointX == 0) && (currentPoint.getY() - pointY == 0)) return true;
        }
        return false;
    }
}
