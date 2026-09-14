package math;

/**
 * Represents a one-dimensional mathematical interval [a, b].
 */
public class Interval {
    /** The lower bound of the interval. */
    public final double a;
    /** The upper bound of the interval. */
    public final double b;

    /**
     * Constructs a new Interval from two values, ensuring a <= b.
     *
     * @param a one endpoint
     * @param b the other endpoint
     */
    public Interval(double a, double b) {
        this.a = Math.min(a, b);
        this.b = Math.max(a, b);
    }

    /**
     * Calculates the midpoint of the interval.
     *
     * @return the midpoint value
     */
    public double midpoint() {
        return a + (b - a) / 2.0;
    }

    /**
     * Calculates the width of the interval.
     *
     * @return the width (b - a)
     */
    public double width() {
        return Math.abs(a - b);
    }

    /**
     * Constructs an invalid (NaN) interval.
     */
    public Interval() {
        this.a = Double.NaN;
        this.b = Double.NaN;
    }

    /**
     * Checks if the interval acts as a point within a given tolerance.
     *
     * @param tolerance the threshold for equality
     * @return true if the interval width is less than the tolerance
     */
    public boolean isPoint(double tolerance) {
        return Math.abs(a - b) < tolerance;
    }

    /**
     * Checks if the interval is invalid (contains NaN).
     *
     * @return true if either endpoint is NaN
     */
    public boolean isNaN() {
        return Double.isNaN(a) || Double.isNaN(b);
    }

    /**
     * Checks if a value is contained within the interval [a, b].
     *
     * @param value the value to check
     * @return true if a <= value <= b
     */
    public boolean contains(double value) {
        return value >= a && value <= b;
    }

    /**
     * Returns a string representation of the interval.
     *
     * @return the interval in [a, b] format
     */
    @Override
    public String toString() {
        return "[" + a + ", " + b + "]";
    }
}
