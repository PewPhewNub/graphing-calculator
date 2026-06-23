package persistence.plotdata;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javafx.scene.paint.Color;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = FunctionPlotData.class,
        name = "FUNCTION"
    ),
    @JsonSubTypes.Type(
        value = ParametricPlotData.class,
        name = "PARAMETRIC"
    ),
    @JsonSubTypes.Type(
        value = ImplicitPlotData.class,
        name = "IMPLICIT"
    )
})
public abstract class PlotData {
    public String name;
    public String color;
}
