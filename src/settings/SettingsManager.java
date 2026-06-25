package settings;

import java.util.ArrayList;

import javafx.application.Application;

public class SettingsManager {
    private ApplicationSettings settings;
    private ArrayList<SettingsListener> listeners = new ArrayList<>();

    public SettingsManager(){
        listeners = new ArrayList<>();
        settings = new ApplicationSettings();
    }

    public SettingsManager(ApplicationSettings settings){
        listeners = new ArrayList<>();
        this.settings = settings;
    }
    public ApplicationSettings getSettings(){
        return settings;
    }

    public void save(){

    }

    public void load(){

    }

    public void addListener(SettingsListener listener){
        listeners.add(listener);
    }
    public void removeListener(SettingsListener listener){
        listeners.remove(listener);
    }

    public void setTheme(Theme theme){
        settings.theme = theme;
        for(SettingsListener listener : listeners){
            listener.themeChanged(theme);
        }
    }
}
