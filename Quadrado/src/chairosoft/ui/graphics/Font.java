/* 
 * Nicholas Saney 
 * 
 * Created: January 25, 2015
 * Modified: January 25, 2015
 * 
 * Font.java
 * Font class definition
 * 
 */

package chairosoft.ui.graphics;


/**
 * An object representing a particular typeface / font.
 */
public class Font
{
    public static class Family
    {
        private Family() { }
        public static final String DIALOG = "Dialog";
        public static final String DIALOG_INPUT = "DialogInput";
        public static final String MONOSPACED = "Monospaced";
        public static final String SANS_SERIF = "SansSerif";
        public static final String SERIF = "Serif";
    }
    public static enum Style
    {
        PLAIN, 
        BOLD, 
        ITALIC, 
        BOLD_ITALIC;
    }
    
    public final String family;
    public final Style style;
    public final int size;
    public Font(String _family, Style _style, int _size)
    {
        this.family = _family;
        this.style = _style;
        this.size = _size;
    }
}