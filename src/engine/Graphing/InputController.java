package engine.Graphing;

import java.util.HashSet;

import engine.rendering.CameraSystem;
import javafx.scene.input.KeyCode;

public class InputController {
    public CameraSystem system;
    
    public double mouseX = 0; 
    public double mouseY = 0;;
    public double pressedX = 0; 
    public double pressedY = 0;
    public double pressedWorldX;
    public double pressedWorldY;
    public double lastMouseX = 0; 
    public double lastMouseY = 0;
    
    public double deltaScrollY = 0;
    public double deltaScrollX = 0;

    public boolean isMouseDown;

    public boolean isShiftDown = false;
    public boolean isCtrlDown = false;
    public boolean isAltDown= false;

    public HashSet<KeyCode> keysPressed;

    public InputController(CameraSystem system){
        this.system = system;
        keysPressed = new HashSet<KeyCode>();
    }
    public void handle(){
        handleKeys();
    }

    public void handleKeys(){
        if (keysPressed.contains(KeyCode.CONTROL)) isCtrlDown = true; else isCtrlDown = false;
        if (keysPressed.contains(KeyCode.SHIFT)) isShiftDown = true; else isShiftDown = false;
        if (keysPressed.contains(KeyCode.ALT)) isAltDown = true; else isAltDown = false;
    }

    public void update(){
        
    }
}
