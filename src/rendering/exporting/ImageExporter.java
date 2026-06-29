package rendering.exporting;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import scene.FunctionGraphScene;
import scene.GraphScene;
import ui.shell.GraphTab;

public final class ImageExporter {
    private ImageExporter(){}

    public static void copyImage(GraphTab tab, ExportOptions options){
        GraphScene exportScene = createExportScene(tab, options.width(), options.height(), options.transparent());
        
        SnapshotParameters params = new SnapshotParameters();
        if(options.transparent())params.setFill(Color.TRANSPARENT);
        
        WritableImage image = exportScene.getGraph().snapshot(params, null);

        ClipboardContent content = new ClipboardContent();
        content.putImage(image);

        Clipboard.getSystemClipboard().setContent(content);
    }

    public static void exportPNG(GraphTab tab, File file, double width, double height, boolean transparent) throws IOException{
        if(file == null) return;
        GraphScene exportScene = createExportScene(tab, width, height, transparent);
        SnapshotParameters params = new SnapshotParameters();
        if(transparent)params.setFill(Color.TRANSPARENT);
        
        WritableImage image = exportScene.getGraph().snapshot(params, null);
        BufferedImage buffered = SwingFXUtils.fromFXImage(image, null);

        ImageIO.write(buffered, "png", file);
    }

    private static GraphScene createExportScene(GraphTab tab, double width, double height, boolean transparent){
        GraphScene newScene = new FunctionGraphScene(width, height, tab.getSettings());
        newScene.getGraph().viewport = tab.getGraphScene().getGraph().viewport.copy();
        newScene.setPlotManager(tab.getGraphScene().getPlotManager());

        if(transparent) newScene.getRenderer().setBackgroundColor(Color.TRANSPARENT);

        newScene.update();
        newScene.render();

        return newScene;
    }
}
