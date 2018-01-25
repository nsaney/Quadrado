package chairosoft.quadrado.ui.graphics;

public enum FontFamily
{
    DEFAULT("Default"),
    MONOSPACED("Monospaced"),
    SANS_SERIF("SansSerif"),
    SERIF("Serif");
    
    public final String name;
    FontFamily(String _name) { this.name = _name; }
}
