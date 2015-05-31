/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
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
    public Font font;
    public DesktopDrawingContext(Graphics _graphics)
    {
        this.graphics = (Graphics2D)_graphics;
        this.font = DesktopFont.create(this.graphics.getFont());
    }
    
    @Override public void close() { this.graphics.dispose(); }
    @Override public boolean isReady() { return this.graphics != null; }
    
    // Color
    @Override public int getColor() { return this.graphics.getColor().getRGB(); }
    @Override public void setColor(int color) { this.graphics.setColor(new java.awt.Color(color, true)); }
    
    // Font
    @Override public Font getFont() { return this.font; }
    @Override public void setFont(Font _font) 
    {
        this.font = _font;
        java.awt.Font awtFont = ((DesktopFont)this.font).getAwtFont();
        this.graphics.setFont(awtFont);
    }
    
    // Font Layout
    @Override public FontLayout getFontLayout(Font font) { return new DesktopFontLayout(font, this.graphics); }
    
    // Transforms
    @Override public void scale(float sx, float sy) { this.graphics.scale(sx, sy); }
    
    // Image
    @Override public void drawImage(DrawingImage drawingImage, int x, int y)
    {
        DesktopDrawingImage desktopDrawingImage = (DesktopDrawingImage)drawingImage;
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