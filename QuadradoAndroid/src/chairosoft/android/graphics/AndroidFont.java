/* 
 * Nicholas Saney 
 * 
 * Created: May 31, 2015
 * 
 * AndroidFont.java
 * AndroidFont class definition
 * 
 */

package chairosoft.android.graphics;

import chairosoft.ui.graphics.Font;

import android.graphics.Typeface;

import java.io.File;

public class AndroidFont extends Font
{
    protected Typeface typeface = null;
    public Typeface getTypeface() { return this.typeface; }
    
    public static int convertFromTypefaceStyle(int typefaceStyle)
    {
        switch (typefaceStyle)
        {
            case Typeface.BOLD: return Font.Style.BOLD;
            case Typeface.ITALIC: return Font.Style.ITALIC;
            case Typeface.BOLD_ITALIC: return Font.Style.BOLD | Font.Style.ITALIC;
            default: return Font.Style.PLAIN;
        }
    }
    
    public static int convertToTypefaceStyle(int style)
    {
        switch (style)
        {
            case Font.Style.BOLD: return Typeface.BOLD;
            case Font.Style.ITALIC: return Typeface.ITALIC;
            case (Font.Style.BOLD | Font.Style.ITALIC): return Typeface.BOLD_ITALIC;
            default: return Typeface.NORMAL;
        }
    }
    
    @Override protected void init(String _family, int _style, int _size)
    {
        int typefaceStyle = AndroidFont.convertToTypefaceStyle(_style);
        this.typeface = Typeface.create(_family, typefaceStyle);
    }
    
    @Override 
    protected void initAndSetAttributes(File fontFile) 
    {
        this.typeface = Typeface.createFromFile(fontFile);
        this.family = fontFile.getName(); // ??? no idea what to put here
        this.style = AndroidFont.convertFromTypefaceStyle(this.typeface.getStyle());
        this.size = 14; // ??? also no idea for this one
    }
    
    @Override 
    public void deriveByStyle(Font baseFont, int derivedStyle)
    {
        int typefaceStyle = AndroidFont.convertToTypefaceStyle(derivedStyle);
        this.typeface = Typeface.create(((AndroidFont)baseFont).typeface, typefaceStyle);
    }
    
    @Override 
    public void deriveBySize(Font baseFont, int derivedSize)
    {
        this.typeface = ((AndroidFont)baseFont).typeface;
    }
    
    @Override 
    public void derive(Font baseFont, int derivedStyle, int derivedSize)
    {
        this.deriveByStyle(baseFont, derivedStyle);
    }
}