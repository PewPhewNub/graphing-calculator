package computation;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import math.ODESolution;
import math.ODESolving;
import math.ODEStatus;
import math.Point;
import parser.EvaluationContext;
import plotting.data.Arrow;
import plotting.data.GridData;
import plotting.data.ODECurveChunk;
import plotting.data.Segment2D;
import plotting.data.curve.ODECurveData;
import plotting.plots.ODEPlot;
import rendering.camera.Viewport;
import rendering.camera.ViewportState;

public class ODEComputer extends AbstractPlotComputer<ODEPlot, ODECurveData>{


    public ODEComputer(ODEPlot plot, ODECurveData data) {
        super(plot, data);
    }

    @Override
    protected void ensureCoverage(Viewport viewport, EvaluationContext context) {
        ViewportState state = new ViewportState(viewport);

        ArrayList<Point> leftPoints = new ArrayList<>(data.getLeftPoints());
        ArrayList<Point> rightPoints = new ArrayList<>(data.getRightPoints());

        ArrayList<ODECurveChunk> leftChunks = new ArrayList<>(data.getLeftChunks());
        ArrayList<ODECurveChunk> rightChunks = new ArrayList<>(data.getRightChunks());

        double leftExtent = data.getLeftExtent();
        double rightExtent = data.getRightExtent();

        if (state.right > rightExtent) {
            rightExtent = extendRight(
                state.right + ODECurveData.EXTEND_BY,
                context,
                rightPoints,
                rightChunks,
                rightExtent
            );
        }

        if (state.left < leftExtent) {
            leftExtent = extendLeft(
                state.left - ODECurveData.EXTEND_BY,
                context,
                leftPoints,
                leftChunks,
                leftExtent
            );
        }

        data.setRightPoints(rightPoints);
        data.setLeftPoints(leftPoints);

        data.setRightChunks(rightChunks);
        data.setLeftChunks(leftChunks);

        data.setRightExtent(rightExtent);
        data.setLeftExtent(leftExtent);
    }

    @Override
    public void invalidate() {
        data.reset();
    }

    public void generateCurveData(Viewport viewport, GridData gridData, EvaluationContext context) {
        ViewportState state = new ViewportState(viewport);
        ArrayList<Arrow> newArrows = new ArrayList<>();
        if(plot.showSlopeField())
        for(Point2D point : gridData.points){
            double slope = plot.getFunction(context).apply(point.getX(), point.getY());
            double angle = Math.atan(slope);
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);

            newArrows.add(new Arrow(point, dx, dy));
        }

        BoundingBox viewportBox = new BoundingBox(
            state.left,
            state.bottom,
            state.worldWidth,
            state.worldHeight
        );

        double minPixelDistance2 = 1;

        ArrayList<Segment2D> visible = new ArrayList<>();

        for (ODECurveChunk chunk : data.getLeftChunks()) {
            visible.addAll(chunk.getSegments());
        }

        ArrayList<Point> pts = data.getRightPoints();

for (int i = 0; i < pts.size() - 1; i++) {
    Point a = pts.get(i);
    Point b = pts.get(i + 1);

    visible.add(new Segment2D(
        new Point2D(a.x, a.y),
        new Point2D(b.x, b.y)
    ));
}

        data.setVisibleSegments(visible);
        data.setArrows(newArrows);
    }

    private void mergeChunkSegments(
    ODECurveChunk chunk,
    Viewport viewport,
    double minPixelDistance2,
    ArrayList<Segment2D> visible
) {
    List<Segment2D> segments = chunk.getSegments();
    if (segments.isEmpty())
        return;

    Point2D anchor = segments.get(0).point1;
    for (int i = 0; i < segments.size(); i++) {
        Segment2D segment = segments.get(i);
        Point2D current = segment.point2;

        double dx = viewport.worldToScreenX(current.getX()) - viewport.worldToScreenX(anchor.getX());
        double dy = viewport.worldToScreenY(current.getY()) - viewport.worldToScreenY(anchor.getY());

        boolean isLast = (i == segments.size() - 1);

        if (dx * dx + dy * dy < minPixelDistance2 && !isLast)
            continue;

        visible.add(new Segment2D(anchor, current));
        anchor = current;
    }
}

    private double extendRight(
        double targetX,
        EvaluationContext context,
        ArrayList<Point> points,
        ArrayList<ODECurveChunk> chunks,
        double currentExtent
    ) {
        Point last = points.get(points.size() - 1);
        int previousEnd = points.size() - 1;

        ODESolution solution = ODESolving.RK4(
            plot.getFunction(context),
            new Point(last.x, last.y),
            0.05,
            targetX
        );

        if (solution.status() != ODEStatus.SUCCESS) {
            return currentExtent;
        }

        List<Point> newPoints = solution.list();
        points.addAll(newPoints.subList(1, newPoints.size()));
        buildChunks(points, chunks, previousEnd);
        return targetX;
    }

    private double extendLeft(
        double targetX,
        EvaluationContext context,
        ArrayList<Point> points,
        ArrayList<ODECurveChunk> chunks,
        double currentExtent
    ) {
        Point last = points.get(points.size() - 1);
        int previousEnd = points.size() - 1;

        ODESolution solution = ODESolving.RK4(
            plot.getFunction(context),
            new Point(last.x, last.y),
            -0.05,
            targetX
        );

        if (solution.status() != ODEStatus.SUCCESS) {
            return currentExtent;
        }

        List<Point> newPoints = solution.list();
        points.addAll(newPoints.subList(1, newPoints.size()));
        buildChunks(points, chunks, previousEnd);
        return targetX;
    }

    private void buildChunks(
        ArrayList<Point> points,
        ArrayList<ODECurveChunk> chunks,
        int fromIndex) {

        int i = fromIndex;
        while (i < points.size() - 1) {
            double chunkStartX = points.get(i).x;
            int j = i + 1;
            while (j < points.size()
                    && Math.abs(points.get(j).x - chunkStartX) < ODECurveData.CHUNK_WIDTH) {
                j++;
            }
            int end = Math.min(j, points.size() - 1);
            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;
            for (int k = i; k <= end; k++) {
                Point p = points.get(k);

                if (!Double.isFinite(p.x) || !Double.isFinite(p.y))
                    continue;

                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            if (minX < Double.MAX_VALUE) {
                ODECurveChunk chunk = new ODECurveChunk(
                    i,
                    end,
                    new BoundingBox(
                        minX,
                        minY,
                        maxX - minX,
                        maxY - minY
                    )
                );
                Point anchor = points.get(i);

                for (int k = i + 1; k <= end; k++) {
                    Point current = points.get(k);
                    if (!Double.isFinite(anchor.x)
                            || !Double.isFinite(anchor.y)
                            || !Double.isFinite(current.x)
                            || !Double.isFinite(current.y))
                        continue;

                    chunk.getSegments().add(
                        new Segment2D(
                            new Point2D(anchor.x, anchor.y),
                            new Point2D(current.x, current.y)
                        )
                    );
                    anchor = current;
                }
                chunks.add(chunk);
            }
            if (end == i)
                end++;
            i = end;
        }
    }

}
