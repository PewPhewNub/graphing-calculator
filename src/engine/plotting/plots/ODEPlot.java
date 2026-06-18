package engine.plotting.plots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiFunction;

import core.math.Core.ODESolution;
import core.math.Core.ODEStatus;
import core.math.Core.Point;
import core.math.ODESolvers.RungeKuttaMethod;
import core.model.GridData;
import engine.rendering.Renderer;
import engine.rendering.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ODEPlot extends Plot {

    public BiFunction<Double, Double, Double> equation;
    ODEStatus statusLeft;
    ODEStatus statusRight;
    public Point initial;
    ArrayList<Point2D> leftBranch;
    ArrayList<Point2D> rightBranch;
    boolean autoGenerate = false;
    ArrayList<Point2D> fullData;
    public final Set<String> knownVariables = Set.of("x", "y");
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
        if(statusLeft == ODEStatus.SUCCESS || statusLeft == ODEStatus.EXCEEDED_MAX_ITERATIONS){
            for(Point i : solutionLeft.list()) leftBranch.add(new Point2D(i.x, i.y));
        }
        if(statusRight == ODEStatus.SUCCESS || statusRight == ODEStatus.EXCEEDED_MAX_ITERATIONS){
            for(Point i : solutionRight.list()) rightBranch.addLast(new Point2D(i.x, i.y));
        }
        fullData = new ArrayList<>();
        regenerateFullData();
        this.color = color;
        
        System.out.println("ODE constructor end " + (System.currentTimeMillis() - t));
    }
    public void extendLeft(){
        if(statusLeft != ODEStatus.SUCCESS) return;
        long t = System.currentTimeMillis();
        Point2D currentEndPoint = leftBranch.getLast();
        ODESolution solutionLeft = RungeKuttaMethod.adaptiveRK4(equation, new Point(currentEndPoint.getX(), currentEndPoint.getY()), -1e-3, currentEndPoint.getX() - 64, 1e-7);
        statusLeft = solutionLeft.status();
        ArrayList<Point> solutionList = solutionLeft.list(); 
        if(statusLeft == ODEStatus.SUCCESS){
            System.out.println(
                "accepted=" + solutionLeft.acceptedSteps() +
                " rejected=" + solutionLeft.rejectedSteps() +
                " points=" + solutionLeft.list().size()
            );
            for(Point i : solutionList) leftBranch.add(new Point2D(i.x, i.y));
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
            System.out.println(
                "accepted=" + solutionRight.acceptedSteps() +
                " rejected=" + solutionRight.rejectedSteps() +
                " points=" + solutionRight.list().size()
            );
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

    public Point2D nearestPoint(double worldX, double worldY, Viewport viewport) {
        if(fullData.isEmpty()) return null;

        // 1. Use binary search to find the index where worldX would fit
        int index = Collections.binarySearch(fullData, new Point2D(worldX, 0), 
                    Comparator.comparingDouble(Point2D::getX));
        
        if (index < 0) index = -(index + 1);
        index = Math.max(0, Math.min(index, fullData.size() - 1));

        // 2. Search outward from that index until the distance starts increasing
        double minD2 = Double.POSITIVE_INFINITY;
        Point2D nearest = null;

        // Check left and right
        for (int i = index; i >= 0; i--) {
            Point2D p = fullData.get(i);
            double d2 = Math.abs(p.getX() - worldX);
            if (d2 < minD2) {
                minD2 = d2;
                nearest = p;
            } else break; // We passed the closest point on this side
        }

        for (int i = index + 1; i < fullData.size(); i++) {
            Point2D p = fullData.get(i);
            double d2 = Math.abs(p.getX() - worldX);
            if (d2 < minD2) {
                minD2 = d2;
                nearest = p;
            } else break; // We passed the closest point on this side
        }

        return nearest;
    }

    public double distanceSquaredFrom(double worldX, double worldY, Viewport viewport) {
        if(fullData.isEmpty()) return Double.POSITIVE_INFINITY;

        // 1. Use binary search to find the index where worldX would fit
        int index = Collections.binarySearch(fullData, new Point2D(worldX, 0), 
                    Comparator.comparingDouble(Point2D::getX));
        
        if (index < 0) index = -(index + 1);
        index = Math.max(0, Math.min(index, fullData.size() - 1));

        // 2. Search outward from that index until the distance starts increasing
        double minD2 = Double.POSITIVE_INFINITY;

        double mouseX = viewport.worldToScreenX(worldX);
        double mouseY = viewport.worldToScreenY(worldY);
        // Check left and right
        for (int i = index; i >= 0; i--) {
            Point2D p = fullData.get(i);
            double dx = viewport.worldToScreenX(p.getX()) - mouseX;
            double dy = viewport.worldToScreenX(p.getY()) - mouseY;
            double d2 = dx*dx + dy*dy;
            if (d2 < minD2) {
                minD2 = d2;
            } else break; // We passed the closest point on this side
        }

        for (int i = index + 1; i < fullData.size(); i++) {
            Point2D p = fullData.get(i);
            double dx = viewport.worldToScreenX(p.getX()) - mouseX;
            double dy = viewport.worldToScreenX(p.getY()) - mouseY;
            double d2 = dx*dx + dy*dy;
            if (d2 < minD2) {
                minD2 = d2;
            } else break; // We passed the closest point on this side
        }

        return minD2;
    }

    public void extendCoverage(double worldMinX, double worldMaxX, double marginX){
        if(!autoGenerate) return;
        boolean hasChanged = false;
        if(!leftBranch.isEmpty() && leftBranch.getLast().getX() > (worldMinX + marginX)){
            extendLeft();
            hasChanged = true;
        }
        if(!rightBranch.isEmpty() && rightBranch.getLast().getX() < (worldMaxX - marginX)){
            extendRight();
            hasChanged = true;
        }
        if(hasChanged)regenerateFullData();
    }
    
    public ArrayList<Point2D> solve(Point2D initial, double stepSize, double maxSteps){
        ArrayList<Point> list = RungeKuttaMethod.adaptiveRK4(equation, new Point(initial.getX(), initial.getY()), stepSize, maxSteps, 1e-7).list();
        ArrayList<Point2D> newList = new ArrayList<>();
        for(Point i : list) newList.add(new Point2D(i.x, i.y));
        return newList;
    }

    public ArrayList<Point2D> list(){
        return fullData;
    }

    public void regenerateFullData(){     
        fullData.clear();
        fullData.addAll(leftBranch);
        Collections.reverse(fullData);
        fullData.add(new Point2D(initial.x, initial.y));
        fullData.addAll(rightBranch);
    }

    public boolean contains(Point2D point){
        double pointX = point.getX();
        double pointY = point.getY();
        for(Point2D currentPoint : list()){
            if((currentPoint.getX() - pointX == 0) && (currentPoint.getY() - pointY == 0)) return true;
        }
        return false;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }
}
