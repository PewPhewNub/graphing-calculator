package main.java.io.github.pewphewnub.persistence;

import java.util.ArrayList;
import main.java.io.github.pewphewnub.persistence.plotdata.GraphElementData;

public class ProjectData {
    public int version = 1;
    public ViewportData viewport;
    public GraphType graphType;
    public ArrayList<GraphElementData> elements = new ArrayList<>();

    public ProjectData(){
        viewport = new ViewportData();
        elements = new ArrayList<>();
        graphType = GraphType.CARTESIAN;
    }
}
