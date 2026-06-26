package persistence;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import scene.FunctionGraphScene;
import scene.GraphScene;
import settings.ApplicationSettings;
import ui.GraphTab;

public final class ProjectIO {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ProjectIO(){}

    public static void save(GraphTab tab, File file) throws IOException{
        ProjectData data = Serializer.serialize(tab.getGraphScene());

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }

    public static GraphTab load(File file) throws IOException{
        ProjectData data =  MAPPER.readValue(file, ProjectData.class);
        GraphTab tab = new GraphTab(file.getName(), new FunctionGraphScene(1200, 900, new ApplicationSettings()));
        Deserializer.apply(data, tab.getGraphScene());
        tab.setProjectFile(file);
        tab.getUiPanel().rebuildEditors();
        return tab;
    }
}
