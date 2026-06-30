package plotting;

public class Variable extends GraphElement{
    String name = "a";
    String value= "1";
    boolean slider = false;

    double min = -10;
    double max = 10;
    double step = 1;

    public Variable(String name){
        this.name = name;
    }
    public Variable(){
        this.name = "a";
    }
    public double getMax() {
        return max;
    }
    public double getMin() {
        return min;
    }
    public String getName() {
        return name;
    }
    public double getStep() {
        return step;
    }
    public double getValue() {
        return Double.parseDouble(value);
    }
    public boolean isSlider() {
        return slider;
    }
    public void setValue(double value) {
        this.value = value + "";
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void setMax(double max) {
        this.max = max;
    }
    public void setMin(double min) {
        this.min = min;
    }
    public void setStep(double step) {
        this.step = step;
    }
    public void setSlider(boolean slider) {
        this.slider = slider;
    }
    public String getString(){
        return value;
    }

    public boolean equals(Variable variable){
        return name.trim().equals(variable.name.trim())&&
               value.equals(variable.value);
    }
    public Variable copy(){
        Variable variable = new Variable();
        variable.name = this.name;
        variable.max = this.max;
        variable.min = this.min;
        variable.slider = this.slider;
        variable.step = this.step;
        variable.value = this.value;
        return variable;
    }
    public boolean copyFrom(GraphElement element){
        if(element == null) return false;
        if(element instanceof Variable variable){
            this.name = variable.name;
            this.max = variable.max;
            this.min = variable.min;
            this.slider = variable.slider;
            this.step = variable.step;
            this.value = variable.value;
            return true;
        }
        return false;
    }
}
