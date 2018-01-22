package chairosoft.quadrado.android.graphics;

import android.graphics.Typeface;
import chairosoft.quadrado.ui.graphics.FontFamily;

public class AndroidFontFamily {
    
    ////// Constructor //////
    private AndroidFontFamily() { throw new UnsupportedOperationException(); }
    
    ////// Static Methods //////
    public static FontFamily fromAndroidTypeface(Typeface typeface) {
        if (Typeface.MONOSPACE.equals(typeface)) {
            return FontFamily.MONOSPACED;
        }
        else if (Typeface.SANS_SERIF.equals(typeface)) {
            return FontFamily.SANS_SERIF;
        }
        else if (Typeface.SERIF.equals(typeface)) {
            return FontFamily.SERIF;
        }
        return FontFamily.DEFAULT;
    }
    
    public static Typeface toAndroidTypeface(FontFamily logicalFamily) {
        switch (logicalFamily) {
            case DEFAULT:
                return Typeface.DEFAULT;
            case MONOSPACED:
                return Typeface.MONOSPACE;
            case SANS_SERIF:
                return Typeface.SANS_SERIF;
            case SERIF:
                return Typeface.SERIF;
            default:
                return null;
        }
    }
    
}
