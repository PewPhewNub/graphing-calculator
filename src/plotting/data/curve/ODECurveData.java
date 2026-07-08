package plotting.data.curve;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import math.ODEStatus;
import math.Point;
import parser.EvaluationContext;
import plotting.data.Arrow;
import plotting.data.ODECurveChunk;
import plotting.data.Segment2D;
import plotting.plots.ODEPlot;

public class ODECurveData extends CurveData{
    
    public static final double CHUNK_WIDTH   = 16.0;
    static final double INIT_EXTENT   = 128.0;
    public static final double EXTEND_BY     = 128.0; // how far to extend each time

    ODEStatus statusLeft;
    ODEStatus statusRight;
    Point2D initial;

    ArrayList<Point> rightPoints = new ArrayList<>();
    ArrayList<Point> leftPoints  = new ArrayList<>();
    ArrayList<ODECurveChunk> rightChunks = new ArrayList<>();
    ArrayList<ODECurveChunk> leftChunks  = new ArrayList<>();

    ArrayList<Arrow> arrows = new ArrayList<>();
    
    double rightExtent;
    double leftExtent;

    public ODECurveData(ODEPlot originalPlot){
        super(originalPlot);
        initialize();
    }
    
    @Override
    public ODECurveData copy(CurveData data) {
        if (!(data instanceof ODECurveData other)) {
            return null;
        }

        ODECurveData copy = new ODECurveData((ODEPlot) originalPlot);

        copy.rightPoints = new ArrayList<>(other.rightPoints);
        copy.leftPoints = new ArrayList<>(other.leftPoints);

        copy.rightChunks = new ArrayList<>(other.rightChunks);
        copy.leftChunks = new ArrayList<>(other.leftChunks);

        copy.rightExtent = other.rightExtent;
        copy.leftExtent = other.leftExtent;

        copy.statusLeft = other.statusLeft;
        copy.statusRight = other.statusRight;

        copy.initial = other.initial;
        copy.arrows = new ArrayList<>(other.arrows);

        copy.setVisibleSegments(new ArrayList<>(other.visibleSegments()));
        copy.setFeaturePoints(new ArrayList<>(other.featurePoints()));

        return copy;
    }

    @Override
    public Point2D targettedPoint(double mouseX, double mouseY, EvaluationContext context) {
        double minDist2 = Double.POSITIVE_INFINITY;
        Point2D nearest = null;

        for (Point point : getCurvePoints()) {
            double dx = point.x - mouseX;
            double dy = point.y - mouseY;
            double dist2 = dx * dx + dy * dy;

            if (dist2 < minDist2) {
                minDist2 = dist2;
                nearest = new Point2D(point.x, point.y);
            }
        }

        return nearest;
    }
    public boolean needsRightExtension(double xMax){
        return xMax > rightExtent;
    }
    public boolean needsLeftExtension(double xMin){
        return xMin < leftExtent;
    }
    public void initialize(){
        rightPoints = new ArrayList<>();
        leftPoints = new ArrayList<>();
        rightChunks = new ArrayList<>();
        leftChunks = new ArrayList<>();
        rightPoints.add(new Point(((ODEPlot)originalPlot).getInitial().x, ((ODEPlot)originalPlot).getInitial().y));
        leftPoints.add(new Point(((ODEPlot)originalPlot).getInitial().x, ((ODEPlot)originalPlot).getInitial().y));
        rightExtent = ((ODEPlot)originalPlot).getInitial().x;
        leftExtent  = ((ODEPlot)originalPlot).getInitial().x;
        initial = new Point2D(((ODEPlot)originalPlot).getInitial().x, ((ODEPlot)originalPlot).getInitial().y);
        featurePoints.add(initial);
        arrows = new ArrayList<>();
    }
    public void appendLeft(ArrayList<Point> list){
        leftPoints.addAll(list);
    }
    public void appendRight(ArrayList<Point> list){
        rightPoints.addAll(list);
    }
    public ArrayList<Point> getCurvePoints(){
        ArrayList<Point> list = new ArrayList<>(leftPoints.reversed());
        list.add(((ODEPlot)originalPlot).getInitial());
        list.addAll(rightPoints);

        return list;
    }
    public void setLeftChunks(ArrayList<ODECurveChunk> leftChunks) {
        this.leftChunks = leftChunks;
    }
    public void setRightChunks(ArrayList<ODECurveChunk> rightChunks) {
        this.rightChunks = rightChunks;
    }
    public void setLeftExtent(double leftExtent) {
        this.leftExtent = leftExtent;
    }
    public void setRightExtent(double rightExtent) {
        this.rightExtent = rightExtent;
    }
    public ArrayList<ODECurveChunk> getLeftChunks() {
        return leftChunks;
    }
    public ArrayList<ODECurveChunk> getRightChunks() {
        return rightChunks;
    }
    public double getRightExtent() {
        return rightExtent;
    }
    public ArrayList<Point> getRightPoints() {
        return rightPoints;
    }
    public double getLeftExtent() {
        return leftExtent;
    }
    public ArrayList<Point> getLeftPoints() {
        return leftPoints;
    }
    public void reset(){
        leftPoints.clear();
        rightPoints.clear();

        leftChunks.clear();
        rightChunks.clear();

        featurePoints.clear();
        visibleSegments.clear();
        arrows.clear();
        initialize();
    }
    public void setLeftPoints(ArrayList<Point> leftPoints) {
        this.leftPoints = leftPoints;
    }
    public void setRightPoints(ArrayList<Point> rightPoints) {
        this.rightPoints = rightPoints;
    }
    public void setArrows(ArrayList<Arrow> arrows) {
        this.arrows = arrows;
    }
    public ArrayList<Arrow> getArrows() {
        return arrows;
    }
}