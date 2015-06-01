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

import chairosoft.ui.geom.FloatPoint2D;
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
    protected Font font;
    protected final java.awt.Polygon awtPolygon;
    public DesktopDrawingContext(Graphics _graphics)
    {
        this.graphics = (Graphics2D)_graphics;
        this.font = DesktopFont.create(this.graphics.getFont());
        this.awtPolygon = new java.awt.Polygon();
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
    public static java.awt.Polygon setAwtPolygonFromPolygon(java.awt.Polygon awtPolygon, Polygon polygon)
    {
        awtPolygon.reset();
        for (FloatPoint2D p : polygon.points)
        {
            int x = (int)p.x;
            int y = (int)p.y;
            awtPolygon.addPoint(x, y);
        }
        return awtPolygon;
    }
    @Override public void drawPolygon(Polygon polygon) 
    {
        DesktopDrawingContext.setAwtPolygonFromPolygon(this.awtPolygon, polygon); 
        this.graphics.drawPolygon(this.awtPolygon); 
    }
    @Override public void fillPolygon(Polygon polygon) 
    {
        DesktopDrawingContext.setAwtPolygonFromPolygon(this.awtPolygon, polygon); 
        this.graphics.fillPolygon(this.awtPolygon); 
    }
    
    // Rectangle
    @Override public void drawRect(int x, int y, int width, int height) { this.graphics.drawRect(x, y, width, height); }
    @Override public void fillRect(int x, int y, int width, int height) { this.graphics.fillRect(x, y, width, height); }
    
    // String
    @Override public void drawString(String text, int x, int y) { this.graphics.drawString(text, x, y); }
}