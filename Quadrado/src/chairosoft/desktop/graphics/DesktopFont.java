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

import java.awt.FontFormatException;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class DesktopFont extends Font
{
    protected java.awt.Font awtFont = null;
    public java.awt.Font getAwtFont() { return this.awtFont; }
    
    @Override
    public boolean isBold() { return this.awtFont.isBold(); }
    
    @Override
    public boolean isItalic() { return this.awtFont.isItalic(); }
    
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
    
    @Override 
    protected void init(String _family, int _style, int _size)
    {
        int awtStyle = DesktopFont.convertToAwtFontStyle(_style);
        this.awtFont = new java.awt.Font(_family, awtStyle, _size);
    }
    
    public static Font create(java.awt.Font _font)
    {
        int style = DesktopFont.convertFromAwtFontStyle(_font.getStyle());
        return Font.create(_font.getFamily(), style, _font.getSize());
    }
    
    @Override 
    protected void initAndSetAttributes(InputStream fontStream, String fontName) 
    {
        try { this.awtFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream); }
        catch (IOException | FontFormatException ex) { throw new RuntimeException(ex); }
        this.family = this.awtFont.getFamily();
        this.style = DesktopFont.convertFromAwtFontStyle(this.awtFont.getStyle());
        this.size = this.awtFont.getSize();
    }
    
    @Override 
    public void deriveByStyle(Font baseFont, int derivedStyle)
    {
        int awtStyle = DesktopFont.convertToAwtFontStyle(derivedStyle);
        this.awtFont = ((DesktopFont)baseFont).awtFont.deriveFont(awtStyle);
    }
    
    @Override 
    public void deriveBySize(Font baseFont, int derivedSize)
    {
        float awtSize = derivedSize;
        this.awtFont = ((DesktopFont)baseFont).awtFont.deriveFont(awtSize);
    }
    
    @Override 
    public void derive(Font baseFont, int derivedStyle, int derivedSize)
    {
        int awtStyle = DesktopFont.convertToAwtFontStyle(derivedStyle);
        float awtSize = derivedSize;
        this.awtFont = ((DesktopFont)baseFont).awtFont.deriveFont(awtStyle, awtSize);
    }
}