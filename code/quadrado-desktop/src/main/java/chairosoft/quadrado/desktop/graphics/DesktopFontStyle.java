package chairosoft.quadrado.desktop.graphics;

import chairosoft.quadrado.ui.graphics.FontStyle;

import java.awt.*;

public class DesktopFontStyle {
    
    ////// Constructor //////
    private DesktopFontStyle() { throw new UnsupportedOperationException(); }
    
    ////// Static Methods //////
    public static FontStyle fromAwtFontStyle(int awtStyle) {
        switch (awtStyle) {
            case Font.BOLD:
                return FontStyle.BOLD;
            case Font.ITALIC:
                return FontStyle.ITALIC;
            case (Font.BOLD | Font.ITALIC):
                return FontStyle.BOLD_ITALIC;
            default:
                return FontStyle.PLAIN;
        }
    }
    
    public static int toAwtFontStyle(FontStyle style) {
        switch (style) {
            case BOLD:
                return Font.BOLD;
            case ITALIC:
                return Font.ITALIC;
            case BOLD_ITALIC:
                return Font.BOLD | Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }
}
