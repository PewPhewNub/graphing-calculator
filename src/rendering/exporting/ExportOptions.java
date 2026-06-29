package rendering.exporting;

public record ExportOptions(
    int width,
    int height,
    boolean transparent,
    boolean clipboard
){}
