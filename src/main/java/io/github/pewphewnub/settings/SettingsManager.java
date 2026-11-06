package io.github.pewphewnub.settings;

import java.util.ArrayList;


public class SettingsManager {
    private ApplicationSettings settings;
    public SettingsManager(){
        settings = new ApplicationSettings();
    }

    public SettingsManager(ApplicationSettings settings){
        this.settings = settings;
    }
    public ApplicationSettings getSettings(){
        return settings;
    }

    public void save(){

    }

    public void load(){

    }
}
