package core.math.Core;
public class Interval{
    public final double a;
    public final double b;

    public Interval(double a, double b){
        this.a = Math.min(a, b); this.b = Math.max(a, b);
    }
    public double midpoint(){
        return a + (b - a)/2.0;
    }
    public double width(){
        return Math.abs(a - b);
    }
    public Interval(){
        this.a = Double.NaN; this.b = Double.NaN;
    }
    public boolean isPoint(double tolerance){
        return Math.abs(a - b) < tolerance;
    }
    public boolean isNaN(){
        return Double.isNaN(a) || Double.isNaN(b);
    }
    public boolean contains(double value){
        return value >= a && value <= b;
    }
    public String toString(){
        return "[" + a + ", " + b + "]"; 
    }
}