package math;

/**
 * Represents a two-dimensional point (x, y).
 */
public class Point {
    /** The x-coordinate. */
    public final double x; 
    /** The y-coordinate. */
    public final double y;

    /**
     * Constructs a new, invalid (NaN) Point.
     */
    public Point(){
        x = Double.NaN; 
        y = Double.NaN;
    }

    /**
     * Constructs a Point with the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Point(double x, double y){
        this.x = x; 
        this.y = y;
    }

    /**
     * Returns a string representation of the point.
     *
     * @return the point in (x, y) format
     */
    @Override
    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    /**
     * Creates a copy of this point.
     *
     * @return a new Point with identical coordinates
     */
    public Point copy(){
        return new Point(x, y);
    }

    /**
     * Checks if this point is equal to another point.
     *
     * @param p the point to compare with
     * @return true if both x and y coordinates are equal
     */
    public boolean equals(Point p){
        return this.x == p.x && this.y == p.y;
    }

    /**
     * Checks if the point is invalid (contains NaN).
     *
     * @return true if either x or y is NaN
     */
    public boolean isNaN(){
        return Double.isNaN(x) || Double.isNaN(y);
    }

    /**
     * Adds another point to this point.
     *
     * @param a the point to add
     * @return a new Point representing the sum
     */
    public Point add(Point a){
        return new Point(a.x + this.x, a.y + this.y);
    }

    /**
     * Returns a hash code for this point.
     *
     * @return the hash code
     */
    @Override
    public int hashCode(){
        return 31 * (int)Math.ceil(x) + (int)Math.floor(y);
    }
}
