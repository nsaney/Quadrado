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
}