/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DrawingContext.java
 * DrawingContext class definition
 * 
 */

package chairosoft.ui.graphics;

import chairosoft.ui.geom.Polygon;
import chairosoft.ui.geom.Rectangle;

import chairosoft.ui.graphics.Font;

import java.util.Arrays;

public abstract class DrawingContext implements AutoCloseable
{
    public abstract void close() throws Exception;
    public abstract boolean isReady();
    
    // Color
    public abstract int getColor();
    public abstract void setColor(int color);
    
    // Font
    public abstract Font getFont();
    public abstract void setFont(Font font);
    
    // Font Layout
    public abstract FontLayout getFontLayout(Font font);
    public final FontLayout getFontLayout() { return this.getFontLayout(this.getFont()); }
    public final WrappedText getWrappedText(String text, int width) { return new WrappedText(text, this.getFontLayout(), width); }
    public final WrappedText getWrappedText(String text, int width, Font font) { return new WrappedText(text, this.getFontLayout(font), width); }
    
    // Transforms
    public abstract void scale(float sx, float sy);
    
    // Image
    public abstract void drawImage(DrawingImage drawingImage, int x, int y);
    
    // Oval
    public abstract void drawOval(int x, int y, int width, int height);
    public final void drawOval(Rectangle r) { this.drawOval(r.x, r.y, r.width, r.height); }
    public abstract void fillOval(int x, int y, int width, int height);
    public final void fillOval(Rectangle r) { this.fillOval(r.x, r.y, r.width, r.height); }
    
    // Polygon
    public abstract void drawPolygon(Polygon polygon);
    public abstract void fillPolygon(Polygon polygon);
    
    // Rectangle
    public abstract void drawRect(int x, int y, int width, int height);
    public final void drawRect(Rectangle r) { this.drawRect(r.x, r.y, r.width, r.height); }
    public abstract void fillRect(int x, int y, int width, int height);
    public final void fillRect(Rectangle r) { this.fillRect(r.x, r.y, r.width, r.height); }
    
    // String
    public abstract void drawString(String text, int x, int y);
    public final void drawWrappedText(WrappedText wrappedText, int x, int y) { this.drawLinesOfText(wrappedText.lines, wrappedText.fontLayout, x, y); }
    public final void drawLinesOfText(String[] lines, int x, int y) { this.drawLinesOfText(Arrays.asList(lines), x, y); }
    public final void drawLinesOfText(Iterable<String> lines, int x, int y) { this.drawLinesOfText(lines, this.getFontLayout(), x, y); }
    public final void drawLinesOfText(String[] lines, FontLayout fontLayout, int x, int y) { this.drawLinesOfText(Arrays.asList(lines), fontLayout, x, y); }
    public final void drawLinesOfText(Iterable<String> lines, FontLayout fontLayout, int x, int y)
    {
        y -= (fontLayout.descent() + fontLayout.leading());
        for (String line : lines)
        {
            y += fontLayout.height();
            this.drawString(line, x, y);
        }
    }
}