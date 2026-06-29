package ui.controls;

import java.util.function.BiFunction;

import interaction.commands.EditPlotCommand;
import plotting.PlotManager;
import plotting.plots.ImplicitPlot;
import plotting.plots.PlotGenerator;
import ui.components.EquationInput;

public class ImplicitPlotEditor extends AbstractPlotEditor{

    private EquationInput box0;

    public String dependent = "y";
    public String independent = "x";

    public ImplicitPlotEditor(PlotManager plotManager, ImplicitPlot plot){
        updatingFields = true;
        this.plotManager = plotManager;
        initialize();
        this.plot = plot;
        colorChooser.setSelectedColor(plot.getColor());

        box0.setLabelText("");
        box0.setFieldText(plot.expression1.trim() + " = " + plot.expression2.trim());
        nameLabel.setText(plot.getName());

        attachListeners();
        updatingFields = false;
    }

    @Override
    protected void initialize() {
        super.initialize();
        box0 = new EquationInput("", 14, "y = x");
        this.getChildren().add(box0);
    }

    @Override
    protected void attachListeners() {
        super.attachListeners();
        box0.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                buildPlot();
            }
        );
    }

    @Override
    protected void buildPlot(){
        String text = box0.getText();
        ImplicitPlot before = (ImplicitPlot)plot.copy();
        BiFunction<Double, Double, Double> f = 
            PlotGenerator.generateBiFunction(text);
        if(f == null){
            return;
        }
        int index = text.indexOf((int)('='));
        if(index == -1) return;
        String exp1 = text.substring(0, index).trim();
        String exp2 = text.substring(index + 1).trim();

        ImplicitPlot after = new ImplicitPlot(nameLabel.getText(), exp1, exp2, f, colorChooser.getSelectedColor());
        if(before.equals(after)) return;
        undoManager.execute(
            new EditPlotCommand(
                plot, 
                before,
                after,
                plotManager
            )
        );
    }

    public void updateFields(){   
        updatingFields = true;
        ImplicitPlot fPlot = (ImplicitPlot)plot;
        colorChooser.setSelectedColor(plot.getColor());

        box0.setFieldText(fPlot.expression1 + " = " + fPlot.expression2);
        
        nameLabel.setText(plot.getName());
        updatingFields = false;
    }
}
