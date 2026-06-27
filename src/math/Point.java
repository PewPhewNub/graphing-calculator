package math;

import java.util.Objects;

public class Point {
    public final double x; public final double y;

    public Point(){
        x = Double.NaN; y = Double.NaN;
    }
    public Point(double x, double y){
        this.x = x; this.y = y;
    }
    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    public Point copy(){
        return new Point(x, y);
    }

    public boolean equals(Point p){
        return this.x == p.x && this.y == p.y;
    }

    public boolean isNaN(){
        return Double.isNaN(x) || Double.isNaN(y);
    }

    public Point add(Point a){
        return new Point(a.x + this.x, a.y + this.y);
    }

    public int hashCode(){
        return 31 * (int)Math.ceil(x) + (int)Math.floor(y);
    }
}
