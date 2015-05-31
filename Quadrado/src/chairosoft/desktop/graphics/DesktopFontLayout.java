/* 
 * Nicholas Saney 
 * 
 * Created: January 27, 2015
 * 
 * DesktopFontLayout.java
 * DesktopFontLayout class definition
 * 
 */

package chairosoft.desktop.graphics;

import chairosoft.ui.graphics.Font;
import chairosoft.ui.graphics.FontLayout;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class DesktopFontLayout extends FontLayout
{
    public final FontMetrics fontMetrics;
    public DesktopFontLayout(Font _font, Graphics _gfx)
    {
        super(_font);
        java.awt.Font awtFont = ((DesktopFont)this.font).getAwtFont();
        this.fontMetrics = _gfx.getFontMetrics(awtFont);
    }
    
    @Override public int height() { return this.fontMetrics.getHeight(); }
    @Override public int ascent() { return this.fontMetrics.getAscent(); }
    @Override public int descent() { return this.fontMetrics.getDescent(); }
    @Override public int leading() { return this.fontMetrics.getLeading(); }
    @Override public int widthOf(String text) { return this.fontMetrics.stringWidth(text); }
}