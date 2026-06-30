package persistence;

import java.util.ArrayList;

import persistence.plotdata.GraphElementData;

public class ProjectData {
    public int version = 1;
    public ViewportData viewport;
    public ArrayList<GraphElementData> elements = new ArrayList<>();

    public ProjectData(){
        viewport = new ViewportData();
        elements = new ArrayList<>();
    }
}
