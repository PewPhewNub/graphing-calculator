package settings;

public class ApplicationSettings {
    public Theme theme;
    public RendererSettings rendererSettings = new RendererSettings();

    public ApplicationSettings(){
        theme = Theme.LIGHT;
    }

    public void setRendererSettings(RendererSettings rendererSettings) {
        this.rendererSettings = rendererSettings;
    }
}
