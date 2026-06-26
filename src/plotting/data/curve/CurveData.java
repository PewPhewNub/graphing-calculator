package plotting.data.curve;

import java.util.ArrayList;

import interaction.InteractionResult;
import javafx.geometry.Point2D;
import plotting.data.Segment2D;
import plotting.plots.AbstractPlot;

public class CurveData{

    AbstractPlot originalPlot;
    ArrayList<Segment2D> visibleSegments;
    ArrayList<Point2D> featurePoints;

    public CurveData(AbstractPlot plot, ArrayList<Segment2D> segments, ArrayList<Point2D> points){
        this.originalPlot = plot;
        this.visibleSegments = segments;
        this.featurePoints = points;
    }

    public CurveData(AbstractPlot plot, ArrayList<Segment2D> segments){
        this.originalPlot = plot;
        this.visibleSegments = segments;
    }

    public AbstractPlot plot(){
        return originalPlot;
    }

    public ArrayList<Segment2D> visibleSegments(){
        return visibleSegments;
    }

    public ArrayList<Point2D> featurePoints(){
        return featurePoints;
    }

    public InteractionResult hitTest(double mouseX, double mouseY){
        Point2D nearestPoint = nearestPoint(mouseX, mouseY);
        
        if(nearestPoint == null) return new InteractionResult(originalPlot, null, null, Double.POSITIVE_INFINITY);
        double dx = nearestPoint.getX() - mouseX;
        double dy = nearestPoint.getY() - mouseY;
        double dist2 = dx*dx + dy*dy;
        return new InteractionResult(originalPlot, this, nearestPoint, dist2);
    }

    public Point2D nearestPoint(double mouseX, double mouseY){
        double minDist2 = Double.POSITIVE_INFINITY;
        Point2D currentNearestPoint = null;
        for(Segment2D seg: visibleSegments){
            Point2D nearestPoint = seg.nearestPoint(mouseX, mouseY);
            double dx = nearestPoint.getX() - mouseX;
            double dy = nearestPoint.getY() - mouseY;
            double dist2 = dx*dx + dy*dy;

            if(minDist2 > dist2){
                minDist2 = dist2;
                currentNearestPoint = nearestPoint;
            }
        }
    
        return currentNearestPoint;
    }

    public double distanceSquared(double mouseX, double mouseY){
        double minDist2 = Double.POSITIVE_INFINITY;
        for(Segment2D seg: visibleSegments){
            Point2D nearestPoint = seg.nearestPoint(mouseX, mouseY);
            double dx = nearestPoint.getX() - mouseX;
            double dy = nearestPoint.getY() - mouseY;
            double dist2 = dx*dx + dy*dy;

            if(minDist2 > dist2){
                minDist2 = dist2;
            }
        }
    
        return minDist2;
    }

    public void addFeaturePoints(ArrayList<Point2D> points){
        this.featurePoints = points;
    }
}
