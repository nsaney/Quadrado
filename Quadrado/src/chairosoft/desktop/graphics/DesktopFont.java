/* 
 * Nicholas Saney 
 * 
 * Created: May 2015
 * 
 * DesktopFont.java
 * DesktopFont class definition
 * 
 */

package chairosoft.desktop.graphics;

import chairosoft.ui.graphics.Font;

public class DesktopFont extends Font
{
    protected java.awt.Font awtFont = null;
    public java.awt.Font getAwtFont() { return this.awtFont; }
    
    public static int convertFromAwtFontStyle(int awtStyle)
    {
        switch (awtStyle)
        {
            case java.awt.Font.BOLD: return Font.Style.BOLD;
            case java.awt.Font.ITALIC: return Font.Style.ITALIC;
            case (java.awt.Font.BOLD | java.awt.Font.ITALIC): return Font.Style.BOLD | Font.Style.ITALIC;
            default: return Font.Style.PLAIN;
        }
    }
    
    public static int convertToAwtFontStyle(int style)
    {
        switch (style)
        {
            case Font.Style.BOLD: return java.awt.Font.BOLD;
            case Font.Style.ITALIC: return java.awt.Font.ITALIC;
            case (Font.Style.BOLD | Font.Style.ITALIC): return java.awt.Font.BOLD | java.awt.Font.ITALIC;
            default: return java.awt.Font.PLAIN;
        }
    }
    
    @Override protected void init(String _family, int _style, int _size)
    {
        int awtStyle = DesktopFont.convertToAwtFontStyle(_style);
        this.awtFont = new java.awt.Font(_family, awtStyle, _size);
    }
    
    public static Font create(java.awt.Font _font)
    {
        int style = DesktopFont.convertFromAwtFontStyle(_font.getStyle());
        return Font.create(_font.getFamily(), style, _font.getSize());
    }
}