package rendering.exporting;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import scene.FunctionGraphScene;
import scene.GraphScene;
import ui.shell.GraphTab;

public final class ImageExporter {
    private ImageExporter(){}
    public static void exportPNG(GraphTab tab, File file, double height, double width) throws IOException{
        if(file == null) return;
        GraphScene exportScene = createExportScene(tab, height, width);
        WritableImage image = exportScene.getGraph().snapshot(new SnapshotParameters(), null);
        BufferedImage buffered = SwingFXUtils.fromFXImage(image, null);

        ImageIO.write(buffered, "png", file);
    }

    private static GraphScene createExportScene(GraphTab tab, double height, double width){
        GraphScene newScene = new FunctionGraphScene(width, height, tab.getSettings());
        newScene.getGraph().viewport = tab.getGraphScene().getGraph().viewport.copy();
        newScene.setPlotManager(tab.getGraphScene().getPlotManager());

        newScene.update();
        newScene.render();

        return newScene;
    }
}
