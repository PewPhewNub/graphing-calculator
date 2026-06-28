package settings;

public class SceneSettings {
    public RendererSettings rendererSettings;
    public ApplicationSettings applicationSettings;
    public SceneSettings(){
        rendererSettings = new RendererSettings();
    }

    public void setRendererSettings(RendererSettings rendererSettings) {
        this.rendererSettings = rendererSettings;
    }

    public void reset(){
        rendererSettings = applicationSettings.rendererSettings;
    }
}
