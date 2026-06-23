package persistence;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import engine.UI.GraphTab;
import engine.scene.FunctionGraphScene;

public final class ProjectIO {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ProjectIO(){}

    public static void save(GraphTab tab, File file) throws IOException{
        ProjectData data = Serializer.serialize(tab);

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }

    public static GraphTab load(File file) throws IOException{
        ProjectData data =  MAPPER.readValue(file, ProjectData.class);
        GraphTab tab = new GraphTab(file.getName(), new FunctionGraphScene(1200, 900));
        Deserializer.apply(data, tab);
        tab.setProjectFile(file);
        return tab;
    }
}
