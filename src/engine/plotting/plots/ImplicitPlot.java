package engine.plotting.plots;

import java.util.ArrayList;
import java.util.function.BiFunction;

import core.model.Segment2D;
import core.model.ViewportState;
import engine.rendering.camera.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ImplicitPlot extends Plot{
    public BiFunction<Double, Double, Double> function;
    public Color color;
    public String name;
    
    public ImplicitPlot(String name, BiFunction<Double, Double, Double> function, Color color){
        this.name = name;
        this.function = function;
        this.color = color;
    }

    public ArrayList<Segment2D> sample(Viewport viewport){ //using marching squares
        ViewportState state = new ViewportState(viewport);
        int maxCells = 512;
        double stepX = state.worldWidth/maxCells;
        double stepY = state.worldHeight/maxCells;
        ArrayList<Segment2D> segments = new ArrayList<>();
        // We need maxCells + 1 points to create maxCells cells (0 to maxCells)
        double[] prevRow = new double[maxCells + 1];
        for (int j = 0; j <= maxCells; j++) {
            prevRow[j] = function.apply(state.left, state.bottom + j * stepY);
        }

        for (int i = 0; i <= maxCells; i++) {
            double x0 = state.left + i * stepX;
            double x1 = state.left + (i + 1) * stepX;
            double[] nextRow = new double[maxCells + 1];

            // Pre-calculate the right column for this strip of cells
            for (int j = 0; j <= maxCells; j++) {
                nextRow[j] = function.apply(x1 + Math.random()*1e-10, state.bottom + j * stepY + Math.random()*1e-10);
            }

            for (int j = 0; j < maxCells; j++) {
                double y0 = state.bottom + j * stepY;
                double y1 = state.bottom + (j + 1) * stepY;

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

                int code = (a << 3) | (b << 2) | (c << 1) | d;

                double tAB = Math.abs(fA)/(Math.abs(fA) + Math.abs(fB));
                double tCB = Math.abs(fC)/(Math.abs(fB) + Math.abs(fC));
                double tDC = Math.abs(fD)/(Math.abs(fC) + Math.abs(fD));
                double tDA = Math.abs(fD)/(Math.abs(fD) + Math.abs(fA));

                Point2D left = new Point2D(x0, tDA*stepY + y0); 
                Point2D right = new Point2D(x1, tCB*stepY + y0);
                Point2D top = new Point2D(tAB*stepX + x0, y1); 
                Point2D bottom = new Point2D(tDC*stepX + x0, y0);

                double center = function.apply(x0 + (x1 - x0)/2, y0 + (y1 - y0)/2);
                switch (code) {
                    case 0: case 15: break;
                    case 1:  case 14: segments.add(new Segment2D(left, bottom)); break; // 0001
                    case 2:  case 13: segments.add(new Segment2D(bottom, right)); break; // 0010
                    case 3:  case 12: segments.add(new Segment2D(left, right)); break;   // 0011
                    case 4:  case 11: segments.add(new Segment2D(right, top)); break;    // 0100
                    case 6:  case 9:  segments.add(new Segment2D(top, bottom)); break;   // 0110
                    case 7:  case 8:  segments.add(new Segment2D(top, left)); break;     // 0111
                    case 10:
                        if(center > 0){
                            segments.add(new Segment2D(right, bottom)); 
                            segments.add(new Segment2D(top, left));
                        }else{
                            segments.add(new Segment2D(right, bottom)); 
                            segments.add(new Segment2D(top, left));    
                        }
                        break;
                    case 5:
                        if(center < 0){
                            segments.add(new Segment2D(right, bottom)); 
                            segments.add(new Segment2D(top, left));
                        }else{
                            segments.add(new Segment2D(right, bottom)); 
                            segments.add(new Segment2D(top, left));    
                        }
                        break;
                    // Cases like 11, 12, 13, 14 are handled by their inverse counterparts
                }
            }
            prevRow = nextRow;
        }  
        return segments; 
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

    @Override
    public Point2D nearestPoint(double worldX, double worldY, Viewport viewport) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nearestPoint'");
    }

    @Override
    public double distanceSquaredFrom(double worldX, double worldY, Viewport viewport) {
        // TODO Auto-generated method stub
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean contains(Point2D point) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contains'");
    }
}
