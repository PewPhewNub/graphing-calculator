package plotting.data;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

public class GridData {
    public List<Point2D> points;
    public double stepX;
    public double stepY;

    public GridData(){
        points = new ArrayList<>();
        stepX = 1;
        stepY = 1;
    }

    public GridData copy(){
        GridData newData = new GridData();
        newData.stepX = stepX;
        newData.stepY = stepY;
        newData.points = new ArrayList<>(List.copyOf(points));
        return newData;
    }
}