package io.github.pewphewnub.settings;

public enum Theme{
    LIGHT(
        ThemeManager.class.getResource(
            Paths.CSS + "light.css"
        ).toExternalForm()),
    DARK(
        ThemeManager.class.getResource(
            Paths.CSS + "dark.css"
        ).toExternalForm());

    public final class Paths{
        public static final String CSS =
            "/io/github/pewphewnub/ui/shell/css/styles/";
        
    }

    public final String value;
    Theme(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }
}