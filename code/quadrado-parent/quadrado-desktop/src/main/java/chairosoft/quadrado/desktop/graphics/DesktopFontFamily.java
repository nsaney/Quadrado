package chairosoft.quadrado.desktop.graphics;

import chairosoft.quadrado.ui.graphics.FontFamily;

import java.awt.Font;

public class DesktopFontFamily {
    
    ////// Constructor //////
    private DesktopFontFamily() { throw new UnsupportedOperationException(); }
    
    ////// Static Methods //////
    public static FontFamily fromAwtFontFamily(String awtFontFamily) {
        switch (awtFontFamily) {
            case Font.MONOSPACED:
                return FontFamily.MONOSPACED;
            case Font.SANS_SERIF:
                return FontFamily.SANS_SERIF;
            case Font.SERIF:
                return FontFamily.SERIF;
            default:
                return FontFamily.DEFAULT;
        }
    }
    
    public static String toAwtFontFamily(FontFamily logicalFamily) {
        switch (logicalFamily) {
            case MONOSPACED:
                return Font.MONOSPACED;
            case SANS_SERIF:
                return Font.SANS_SERIF;
            case SERIF:
                return Font.SERIF;
            case DEFAULT:
            default:
                return null;
        }
    }
    
    
}
