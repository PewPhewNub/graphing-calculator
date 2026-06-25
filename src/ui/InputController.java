package ui;

import java.util.HashSet;

import javafx.scene.input.KeyCode;
import rendering.camera.CameraSystem;

public class InputController {
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

    public boolean isShiftDown = false;
    public boolean isCtrlDown = false;
    public boolean isAltDown= false;

    public boolean mouseMoved = false;
    public boolean mousePressed = false;
    public boolean mouseReleased = false;
    public boolean mouseDown = false;

    public HashSet<KeyCode> keysPressed;

    public InputController(){
        keysPressed = new HashSet<KeyCode>();
    }
    public void update(){
        handleKeys();
    }
    public void clearFrameEvents(){
        mousePressed = false;
        mouseReleased = false;
        mouseMoved = false;

        deltaScrollX = 0;
        deltaScrollY = 0;
    }

    public void handleKeys(){
        if (keysPressed.contains(KeyCode.CONTROL)) isCtrlDown = true; else isCtrlDown = false;
        if (keysPressed.contains(KeyCode.SHIFT)) isShiftDown = true; else isShiftDown = false;
        if (keysPressed.contains(KeyCode.ALT)) isAltDown = true; else isAltDown = false;
    }
}
