package core.math.Core;
import java.util.function.*;

public class StandardFunctions {
    public static Function<Double, Double> identity = x -> x;
    public static Function<Double, Double> square = x -> x * x;
    public static Function<Double, Double> cubeRoot = x -> {
        double base = x - 1;
        return base < 0 ? -Math.pow(-base, 1.0 / 3.0) : Math.pow(base, 1.0 / 3.0);
    };
    public static Function<Double, Double> exponential = x -> Math.exp(x);
    public static Function<Double, Double> logarithm = x -> Math.log(x);
    public static Function<Double, Double> sine = x -> Math.sin(x);
    public static Function<Double, Double> cosine = x -> Math.cos(x);
    public static Function<Double, Double> tangent = x -> Math.tan(x);
    public static Function<Double, Double> arcsin = x -> Math.asin(x);
    public static Function<Double, Double> arccos = x -> Math.acos(x);
    public static Function<Double, Double> arctan = x -> Math.atan(x);
}
