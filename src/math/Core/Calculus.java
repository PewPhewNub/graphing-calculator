package math.Core;
import java.util.function.Function;

public class Calculus {
    public static Function<Double, Double> derivative(Function<Double,Double> f, double h) {
        return x -> (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

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
