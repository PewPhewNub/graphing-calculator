package io.github.pewphewnub.settings;

public class ApplicationSettings {
    public ThemeManager themeManager;
    public RendererSettings rendererSettings = new RendererSettings();

    public ApplicationSettings(){
        themeManager = new ThemeManager();
    }

    public void setRendererSettings(RendererSettings rendererSettings) {
        this.rendererSettings = rendererSettings;
    }
}
