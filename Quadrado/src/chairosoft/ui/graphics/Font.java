/* 
 * Nicholas Saney 
 * 
 * Created: January 25, 2015
 * 
 * Font.java
 * Font class definition
 * 
 */

package chairosoft.ui.graphics;

import chairosoft.dependency.Dependencies;

/**
 * An object representing a particular typeface / font.
 */
public abstract class Font
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
    
    public static class Style
    {
        private Style() { }
        public static final int PLAIN = 0;
        public static final int BOLD = 1;
        public static final int ITALIC = 2;
    }
    
    protected String family = null;
    public String getFamily() { return this.family; }
    
    protected int style = 0;
    public int getStyle() { return this.style; }
    
    protected int size = 0;
    public int getSize() { return this.size; }
    
    protected abstract void init(String _family, int _style, int _size);
    public static Font create(String _family, int _style, int _size)
    {
        Font result = Dependencies.getNew(Font.class);
        result.family = _family;
        result.style = _style;
        result.size = _size;
        result.init(_family, _style, _size);
        return result;
    }
}