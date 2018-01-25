package chairosoft.quadrado.android.graphics;

import android.graphics.Typeface;
import chairosoft.quadrado.ui.graphics.FontStyle;

public class AndroidFontStyle {
    
    ////// Constructor //////
    private AndroidFontStyle() { throw new UnsupportedOperationException(); }
    
    ////// Static Methods //////
    public static FontStyle fromTypefaceStyle(int typefaceStyle)
    {
        switch (typefaceStyle)
        {
            case Typeface.BOLD: return FontStyle.BOLD;
            case Typeface.ITALIC: return FontStyle.ITALIC;
            case Typeface.BOLD_ITALIC: return FontStyle.BOLD_ITALIC;
            default: return FontStyle.PLAIN;
        }
    }
    
    public static int toTypefaceStyle(FontStyle style)
    {
        switch (style)
        {
            case BOLD: return Typeface.BOLD;
            case ITALIC: return Typeface.ITALIC;
            case BOLD_ITALIC: return Typeface.BOLD_ITALIC;
            default: return Typeface.NORMAL;
        }
    }
    
}
