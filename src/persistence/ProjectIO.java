package persistence;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import scene.FunctionGraphScene;
import scene.ODEGraphScene;
import settings.ApplicationSettings;
import ui.shell.GraphTab;

public final class ProjectIO {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ProjectIO(){}

    public static void save(GraphTab tab, File file) throws IOException{
        ProjectData data = Serializer.serialize(tab.getGraphScene());

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }

    public static GraphTab load(File file) throws IOException{
        ProjectData data =  MAPPER.readValue(file, ProjectData.class);
        GraphTab tab = switch(data.graphType){
            case CARTESIAN -> new GraphTab(file.getName(), new FunctionGraphScene(new ApplicationSettings()));
            case ODE -> new GraphTab(file.getName(), new ODEGraphScene(new ApplicationSettings()));
        };
        Deserializer.apply(data, tab.getGraphScene());
        tab.setProjectFile(file);
        return tab;
    }
}
