package engine.plotting.plots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

import core.model.ImplicitChunk;
import core.model.Segment2D;
import core.model.ViewportState;
import engine.rendering.camera.Viewport;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ImplicitPlot extends Plot implements CartesianPlot{
    public BiFunction<Double, Double, Double> function;
    public Color color;
    public String name;
    public HashMap<Point2D, ImplicitChunk> chunks;
    public String expression1;
    public String expression2;
    public final double CHUNK_SIZE = 16;
    private final double BASE_SIZE = 16;
    private final int maxCellsPerSide = 128;
    
    public ImplicitPlot(String name, BiFunction<Double, Double, Double> function, Color color){
        this.name = name;
        this.function = function;
        this.color = color;

        chunks = new HashMap<>();
    }

    public ArrayList<Segment2D> marchingSquares(ImplicitChunk chunk, int LOD){
        ArrayList<Segment2D> segments = new ArrayList<>();

        double left = chunk.bounds.getMinX();
        double bottom = chunk.bounds.getMinY();

        double step = BASE_SIZE / (1 << LOD);

        double stepX = step;
        double stepY = step;

        int maxCells = (int)(chunk.bounds.getHeight()/step);
        if(maxCells > maxCellsPerSide){
            maxCells = maxCellsPerSide;
            step = chunk.bounds.getHeight() / (double)maxCells;
        }
        // We need maxCells + 1 points to create maxCells cells (0 to maxCells)
        double[] prevRow = new double[maxCells + 1];
        for (int j = 0; j <= maxCells; j++) {
            prevRow[j] = function.apply(left, bottom + j * stepY);
        }

        for (int i = 0; i < maxCells; i++) {
            double x0 = left + i * stepX;
            double x1 = left + (i + 1) * stepX;
            double[] nextRow = new double[maxCells + 1];

            // Pre-calculate the new Point2D(x1, tCB*stepY + y0) column for this strip of cells
            for (int j = 0; j <= maxCells; j++) {
                nextRow[j] = function.apply(x1, bottom + j * stepY);
            }

            for (int j = 0; j < maxCells; j++) {
                double y0 = bottom + j * stepY;
                double y1 = bottom + (j + 1) * stepY;

                // Corners: A=TL, B=TR, C=BR, D=BL
                // Mapping based on your grid:
                double fA = prevRow[j + 1]; 
                double fB = nextRow[j + 1]; 
                double fC = nextRow[j];     
                double fD = prevRow[j];     

                int a = (fA > 0) ? 1 : 0;
                int b = (fB > 0) ? 1 : 0;
                int c = (fC > 0) ? 1 : 0;
                int d = (fD > 0) ? 1 : 0;

                if(a==b && b==c && c==d) continue;

                int code = (a << 3) | (b << 2) | (c << 1) | d;

                double tAB = Math.abs(fA)/(Math.abs(fA) + Math.abs(fB));
                double tCB = Math.abs(fC)/(Math.abs(fB) + Math.abs(fC));
                double tDC = Math.abs(fD)/(Math.abs(fC) + Math.abs(fD));
                double tDA = Math.abs(fD)/(Math.abs(fD) + Math.abs(fA));

                double center = function.apply(x0 + (x1 - x0)/2, y0 + (y1 - y0)/2);
                switch (code) {
                    case 0: case 15: break;
                    case 1:  case 14: segments.add(new Segment2D(new Point2D(x0, tDA*stepY + y0), new Point2D(tDC*stepX + x0, y0))); break; // 0001
                    case 2:  case 13: segments.add(new Segment2D(new Point2D(tDC*stepX + x0, y0), new Point2D(x1, tCB*stepY + y0))); break; // 0010
                    case 3:  case 12: segments.add(new Segment2D(new Point2D(x0, tDA*stepY + y0), new Point2D(x1, tCB*stepY + y0))); break;   // 0011
                    case 4:  case 11: segments.add(new Segment2D(new Point2D(x1, tCB*stepY + y0), new Point2D(tAB*stepX + x0, y1))); break;    // 0100
                    case 6:  case 9:  segments.add(new Segment2D(new Point2D(tAB*stepX + x0, y1), new Point2D(tDC*stepX + x0, y0))); break;   // 0110
                    case 7:  case 8:  segments.add(new Segment2D(new Point2D(tAB*stepX + x0, y1), new Point2D(x0, tDA*stepY + y0))); break;     // 0111
                    case 10:
                        if(center > 0){
                            segments.add(new Segment2D(new Point2D(x1, tCB*stepY + y0), new Point2D(tDC*stepX + x0, y0))); 
                            segments.add(new Segment2D(new Point2D(tAB*stepX + x0, y1), new Point2D(x0, tDA*stepY + y0)));
                        }else{
                            segments.add(new Segment2D(new Point2D(x1, tCB*stepY + y0), new Point2D(tDC*stepX + x0, y0))); 
                            segments.add(new Segment2D(new Point2D(tAB*stepX + x0, y1), new Point2D(x0, tDA*stepY + y0)));    
                        }
                        break;
                    case 5:
                        if(center < 0){
                            segments.add(new Segment2D(new Point2D(x1, tCB*stepY + y0), new Point2D(tDC*stepX + x0, y0))); 
                            segments.add(new Segment2D(new Point2D(tAB*stepX + x0, y1), new Point2D(x0, tDA*stepY + y0)));
                        }else{
                            segments.add(new Segment2D(new Point2D(x1, tCB*stepY + y0), new Point2D(tDC*stepX + x0, y0))); 
                            segments.add(new Segment2D(new Point2D(tAB*stepX + x0, y1), new Point2D(x0, tDA*stepY + y0)));    
                        }
                        break;
                    // Cases like 11, 12, 13, 14 are handled by their inverse counterparts
                }
            }
            prevRow = nextRow;
        }  
        return segments; 
    }

    public void ensureCoverage(Viewport viewport){
        ViewportState state = new ViewportState(viewport);

        int LOD = calculateLOD(viewport);

        int minChunkX = (int)(Math.floor(state.left/CHUNK_SIZE));
        int maxChunkX = (int)(Math.floor(state.right/CHUNK_SIZE));
        int minChunkY = (int)(Math.floor(state.bottom/CHUNK_SIZE));
        int maxChunkY = (int)(Math.floor(state.top/CHUNK_SIZE));

        for(int cx = minChunkX; cx <= maxChunkX; cx++){
            for(int cy = minChunkY; cy <= maxChunkY; cy++){
                Point2D key = new Point2D(cx, cy);

                if(!chunks.containsKey(key)) {
                    generateChunk(cx, cy, LOD);
                    continue;
                }

                ImplicitChunk chunk = chunks.get(key);
                if(!chunk.generated) continue;
                if(chunk.LOD != LOD){
                    generateChunk(cx, cy, LOD);
                }
            }
        }
    }

    public int calculateLOD(Viewport viewport) {
        ViewportState state = new ViewportState(viewport);

        double worldUnitsPerPixel =
            state.worldWidth / state.viewportWidth;

        double desiredStep =
            worldUnitsPerPixel * 4.0;

        int lod = (int)Math.round(
            Math.log(BASE_SIZE / desiredStep) / Math.log(2)
        );

        return Math.min(Math.max(0, lod), 4);
    }

    public void generateChunk(double cx, double cy, int LOD){
        ImplicitChunk chunk = new ImplicitChunk();
        chunk.bounds = new BoundingBox(cx * CHUNK_SIZE, cy*CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
        ArrayList<Segment2D> segments = marchingSquares(chunk, LOD);
        if(segments== null){
            chunk.generated = false;
            return;
        }
        if(segments.isEmpty()){
            chunk.generated = true;
            chunk.hasCurve = false;
            return;
        }
        chunk.segments = segments;
        chunk.LOD = LOD;
        chunk.generated = true;

        chunks.put(new Point2D(cx, cy), chunk);
    }

    public boolean sample(double x, double y){ //using marching squares
        return Math.abs(function.apply(x, y)) < 1e-6;
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public String getName() {
        return name;
    }
}
