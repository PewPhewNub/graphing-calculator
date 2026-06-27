package interaction;

import java.util.ArrayDeque;
import java.util.Deque;

import interaction.commands.Command;

public class UndoManager {
    Deque<Command> undoStack;
    Deque<Command> redoStack;

    public UndoManager(){
        undoStack = new ArrayDeque<>();
        redoStack = new ArrayDeque<>();
    }
    public void execute(Command command){
        command.execute();
        undoStack.addFirst(command);
        redoStack.clear();
    }

    public void undo(){
        if(undoStack.peekFirst() != null){
            Command last = undoStack.getFirst();
            last.undo();
            undoStack.removeFirst();
            redoStack.addFirst(last);
        }
    }

    public void redo(){
        if(redoStack.peekFirst() != null){
            Command last = redoStack.getFirst();
            redoStack.removeFirst();
            last.execute();
            undoStack.addFirst(last);
        }
    }

    public boolean canUndo(){
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo(){
        return !redoStack.isEmpty();
    }
    public void clearStacks(){
        redoStack.clear();
        undoStack.clear();
    }
}
