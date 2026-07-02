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
    protected void invalidate() {
        data.reset();
    }

    public void generateCurveData(Viewport viewport, EvaluationContext context) {
        ViewportState state = new ViewportState(viewport);

        BoundingBox viewportBox = new BoundingBox(
            state.left,
            state.bottom,
            state.worldWidth,
            state.worldHeight
        );
        double minPixelDistance2 = 4;

        ArrayList<Segment2D> visible = new ArrayList<>();
        for (ODECurveChunk chunk : data.getLeftChunks()) {
            if (!viewportBox.intersects(chunk.bounds))
                continue;
            for (Segment2D segment : chunk.getSegments()) {
                Point2D p1 = segment.point1;
                Point2D p2 = segment.point2;
                double dx =
                    viewport.worldToScreenX(p2.getX())
                - viewport.worldToScreenX(p1.getX());

                double dy =
                    viewport.worldToScreenY(p2.getY())
                - viewport.worldToScreenY(p1.getY());

                if (dx * dx + dy * dy < minPixelDistance2)
                    continue;

                visible.add(segment);
            }
        }

        for (ODECurveChunk chunk : data.getRightChunks()) {

            if (!viewportBox.intersects(chunk.bounds))
                continue;

            for (Segment2D segment : chunk.getSegments()) {

                Point2D p1 = segment.point1;
                Point2D p2 = segment.point2;

                double dx =
                    viewport.worldToScreenX(p2.getX())
                - viewport.worldToScreenX(p1.getX());

                double dy =
                    viewport.worldToScreenY(p2.getY())
                - viewport.worldToScreenY(p1.getY());

                if (dx * dx + dy * dy < minPixelDistance2)
                    continue;

                visible.add(segment);
            }
        }

        data.setVisibleSegments(visible);
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

        ODESolution solution = ODESolving.adaptiveRK4(
            plot.getFunction(context),
            new Point(last.x, last.y),
            0.05,
            targetX,
            1e-3
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

        ODESolution solution = ODESolving.adaptiveRK4(
            plot.getFunction(context),
            new Point(last.x, last.y),
            -0.05,
            targetX,
            1e-3
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
