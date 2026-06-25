package interaction.commands;

import interaction.state.FunctionPlotState;
import plotting.plots.FunctionPlot;

public class EditFunctionPlotCommand implements Command{
    private final FunctionPlot plot;
    private final FunctionPlotState state1;
    private final FunctionPlotState state2;

    public EditFunctionPlotCommand(FunctionPlot plot, FunctionPlotState state1, FunctionPlotState state2){
        this.plot = plot;
        this.state1 = state1;
        this.state2 = state2;
    }

    @Override
    public void execute() {
        plot.update(state2.expression, state2.dependent, state2.independent, state2.color);
    }

    @Override
    public void undo() {
        plot.update(state1.expression, state1.dependent, state1.independent, state1.color);
    }
}
