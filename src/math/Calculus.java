package math;
import java.util.function.Function;

/**
 * Utility class providing calculus-related operations, such as numerical
 * differentiation and integration.
 */
public class Calculus {
    /**
     * Approximates the derivative of a function using the central difference method.
     *
     * @param f the function to differentiate
     * @param h the step size for the approximation
     * @return a function representing the derivative
     */
    public static Function<Double, Double> derivative(Function<Double,Double> f, double h) {
        return x -> (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

    /**
     * Approximates the definite integral of a function using the trapezoidal rule.
     *
     * @param f the function to integrate
     * @param a the lower bound of integration
     * @param b the upper bound of integration
     * @param n the number of sub-intervals
     * @return a function representing the integral approximation
     */
    public static Function<Double, Double> integral(Function<Double, Double> f, double a, double b, int n){
        double h = (b - a) / n;
        return x -> {
            double sum = 0.5 * (f.apply(a) + f.apply(b));
            for (int i = 1; i < n; i++) {
                sum += f.apply(a + i * h);
            }
            return sum * h;
        };
    }
}
