package engine.interaction.commands;

public interface Command {
    public void execute();
    public void undo();
}
