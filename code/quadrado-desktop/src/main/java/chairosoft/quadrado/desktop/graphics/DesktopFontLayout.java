/* 
 * Nicholas Saney 
 * 
 * Created: January 27, 2015
 * 
 * DesktopFontLayout.java
 * DesktopFontLayout class definition
 * 
 */

package chairosoft.quadrado.desktop.graphics;

import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.FontLayout;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class DesktopFontLayout extends FontLayout
{
    public final FontMetrics fontMetrics;
    public DesktopFontLayout(FontFace _font, Graphics _gfx)
    {
        super(_font);
        java.awt.Font awtFont = ((DesktopFontFace)this.fontFace).getAwtFont();
        this.fontMetrics = _gfx.getFontMetrics(awtFont);
    }
    
    @Override public int ascent() { return this.fontMetrics.getMaxAscent(); }
    @Override public int descent() { return this.fontMetrics.getMaxDescent(); }
    @Override public int leading() { return this.fontMetrics.getLeading(); }
    @Override public int widthOf(String text) { return this.fontMetrics.stringWidth(text); }
}