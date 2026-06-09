package math.Core;

public class SolverState {
    public double current;
    public double previous;
    public double previous2; 

    public SolverState(double current, double previous, double previous2){
        this.current = current;
        this.previous = previous;
        this.previous2 = previous2;
    }

    public boolean isInvalid(){
        return Double.isNaN(current) || Double.isNaN(previous) || Double.isNaN(previous2);
    }

    public SolverState copy(){
        return new SolverState(current, previous, previous2);
    }
}
