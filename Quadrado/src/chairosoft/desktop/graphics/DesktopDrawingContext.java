/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * Modified: January 25, 2015
 * 
 * DesktopDrawingContext.java
 * DesktopDrawingContext class definition
 * 
 */

package chairosoft.desktop.graphics;

import chairosoft.ui.geom.IntPoint2D;
import chairosoft.ui.geom.Polygon;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;
import chairosoft.ui.graphics.Font;
import chairosoft.ui.graphics.FontLayout;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class DesktopDrawingContext extends DrawingContext
{
    public final Graphics2D graphics;
    public DesktopDrawingContext(Graphics _graphics)
    {
        this.graphics = (Graphics2D)_graphics;
    }
    
    @Override public void close() { this.graphics.dispose(); }
    @Override public boolean isReady() { return this.graphics != null; }
    
    // Color
    @Override public int getColor() { return this.graphics.getColor().getRGB(); }
    @Override public void setColor(int color) { this.graphics.setColor(new java.awt.Color(color, true)); }
    
    // Font
    public static Font.Style convertFromAwtFontStyle(int style)
    {
        switch (style)
        {
            case java.awt.Font.BOLD: return Font.Style.BOLD;
            case java.awt.Font.ITALIC: return Font.Style.ITALIC;
            case (java.awt.Font.BOLD | java.awt.Font.ITALIC): return Font.Style.BOLD_ITALIC;
            default: return Font.Style.PLAIN;
        }
    }
    public static int convertToAwtFontStyle(Font.Style style)
    {
        switch (style)
        {
            case BOLD: return java.awt.Font.BOLD;
            case ITALIC: return java.awt.Font.ITALIC;
            case BOLD_ITALIC: return java.awt.Font.BOLD | java.awt.Font.ITALIC;
            default: return java.awt.Font.PLAIN;
        }
    }
    public static Font convertFromAwtFont(java.awt.Font font)
    {
        Font.Style fontStyle = DesktopDrawingContext.convertFromAwtFontStyle(font.getStyle());
        return new Font(font.getFamily(), fontStyle, font.getSize());
    }
    public static java.awt.Font convertToAwtFont(Font font)
    {
        int fontStyle = DesktopDrawingContext.convertToAwtFontStyle(font.style);
        return new java.awt.Font(font.family, fontStyle, font.size);
    }
    @Override public Font getFont() { return DesktopDrawingContext.convertFromAwtFont(this.graphics.getFont()); }
    @Override public void setFont(Font font) { this.graphics.setFont(DesktopDrawingContext.convertToAwtFont(font)); }
    
    // Font Layout
    @Override public FontLayout getFontLayout(Font font) { return new DesktopFontLayout(font, this.graphics); }
    
    // Transforms
    @Override public void scale(float sx, float sy) { this.graphics.scale(sx, sy); }
    
    // Image
    @Override public void drawImage(DrawingImage drawingImage, int x, int y)
    {
        if (drawingImage instanceof DesktopDrawingImage)
        {
            this.drawImage((DesktopDrawingImage)drawingImage, x, y);
        }
    }
    public void drawImage(DesktopDrawingImage desktopDrawingImage, int x, int y)
    {
        this.graphics.drawImage(desktopDrawingImage.image, x, y, null);
    }
    
    // Oval
    @Override public void drawOval(int x, int y, int width, int height) { this.graphics.drawOval(x, y, width, height); }
    @Override public void fillOval(int x, int y, int width, int height) { this.graphics.fillOval(x, y, width, height); }
    
    // Polygon
    public static java.awt.Polygon convertPolygon(Polygon polygon)
    {
        int npoints = polygon.points.size();
        int[] xpoints = new int[npoints];
        int[] ypoints = new int[npoints];
        for (int i = 0; i < npoints; ++i)
        {
            IntPoint2D p = polygon.points.get(i).asIntPoint2D();
            xpoints[i] = p.x;
            ypoints[i] = p.y;
        }
        return new java.awt.Polygon(xpoints, ypoints, npoints);
    }
    @Override public void drawPolygon(Polygon polygon) { this.graphics.drawPolygon(DesktopDrawingContext.convertPolygon(polygon)); }
    @Override public void fillPolygon(Polygon polygon) { this.graphics.fillPolygon(DesktopDrawingContext.convertPolygon(polygon)); }
    
    // Rectangle
    @Override public void drawRect(int x, int y, int width, int height) { this.graphics.drawRect(x, y, width, height); }
    @Override public void fillRect(int x, int y, int width, int height) { this.graphics.fillRect(x, y, width, height); }
    
    // String
    @Override public void drawString(String text, int x, int y) { this.graphics.drawString(text, x, y); }
}