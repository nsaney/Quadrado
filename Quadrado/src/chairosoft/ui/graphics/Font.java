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
import chairosoft.util.Loading;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

/**
 * An object representing a particular typeface / font.
 */
public abstract class Font
{
    public static final String EMBEDDED_FONT_FOLDER = "font";
    
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
    public abstract boolean isBold();
    public abstract boolean isItalic();
    
    protected int size = 0;
    public int getSize() { return this.size; }
    
    protected void setAttributes(String _family, int _style, int _size)
    {
        this.family = _family;
        this.style = _style;
        this.size = _size;
    }
    
    protected abstract void init(String _family, int _style, int _size);
    public static Font create(String _family, int _style, int _size)
    {
        Font result = Dependencies.getNew(Font.class);
        result.setAttributes(_family, _style, _size);
        result.init(_family, _style, _size);
        return result;
    }
    
    protected abstract void initAndSetAttributes(InputStream fontStream, String fontName);
    public static Font createFromStream(InputStream fontStream, String fontName)
    {
        Font result = Dependencies.getNew(Font.class);
        result.initAndSetAttributes(fontStream, fontName);
        return result;
    }
    
    public static Font createFromEmbeddedFont(String fontName)
    {
        try
        {
            String fontLocation = Loading.getPathInFolder(EMBEDDED_FONT_FOLDER, fontName);
            InputStream fontResourceStream = Loading.getInputStreamFromPath(fontLocation);
            return Font.createFromStream(fontResourceStream, fontName);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    protected abstract void deriveByStyle(Font baseFont, int derivedStyle);
    public Font deriveByStyle(int derivedStyle)
    {
        Font result = Dependencies.getNew(Font.class);
        result.setAttributes(this.family, derivedStyle, this.size);
        result.deriveByStyle(this, derivedStyle);
        return result;
    }
    
    protected abstract void deriveBySize(Font baseFont, int derivedSize);
    public Font deriveBySize(int derivedSize)
    {
        Font result = Dependencies.getNew(Font.class);
        result.setAttributes(this.family, this.style, derivedSize);
        result.deriveBySize(this, derivedSize);
        return result;
    }
    
    protected abstract void derive(Font baseFont, int derivedStyle, int derivedSize);
    public Font derive(int derivedStyle, int derivedSize)
    {
        Font result = Dependencies.getNew(Font.class);
        result.setAttributes(this.family, derivedStyle, derivedSize);
        result.derive(this, derivedStyle, derivedSize);
        return result;
    }
}