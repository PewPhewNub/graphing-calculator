package plotting;

import rendering.graph.Graph;

public abstract class GraphElement {
    String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public abstract GraphElement copy();
    public abstract boolean copyFrom(GraphElement element);
}
