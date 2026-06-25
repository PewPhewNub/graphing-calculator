package settings;

import javafx.scene.paint.Color;

public class ThemeManager {
    public static ThemeColors getColors(Theme theme){

        return switch(theme){

            case LIGHT -> new ThemeColors(
                Color.WHITE,
                Color.WHITESMOKE,
                Color.LIGHTGRAY,
                Color.BLACK
            );

            case DARK -> new ThemeColors(
                Color.rgb(40,40,40),
                Color.rgb(55,55,55),
                Color.GRAY,
                Color.WHITE
            );
        };
    }
}
