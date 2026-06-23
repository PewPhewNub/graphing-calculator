package persistence;

import java.util.ArrayList;

import persistence.plotdata.PlotData;

public class ProjectData {
    public int version = 1;
    public ViewportData viewport;
    public ArrayList<PlotData> plots = new ArrayList<>();

    public ProjectData(){
        viewport = new ViewportData();
        plots = new ArrayList<>();
    }
}
