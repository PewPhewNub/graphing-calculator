package persistence.plotdata;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
    ),
    @JsonSubTypes.Type(
        value = PolarPlotData.class,
        name = "POLAR"
    ),
    @JsonSubTypes.Type(
        value = VariableData.class,
        name = "VARIABLE"
    ),
})
public abstract class GraphElementData {
    public String name;
}
