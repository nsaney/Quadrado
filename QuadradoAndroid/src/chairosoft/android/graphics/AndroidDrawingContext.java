/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * AndroidDrawingContext.java
 * AndroidDrawingContext class definition
 * 
 */

package chairosoft.android.graphics;

import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.IntPoint2D;
import chairosoft.ui.geom.Polygon;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;
import chairosoft.ui.graphics.Font;
import chairosoft.ui.graphics.FontLayout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

public class AndroidDrawingContext extends DrawingContext
{
    public final Canvas canvas;
    protected final Paint paint;
    protected Font font;
    protected final Path path;
    public AndroidDrawingContext(Canvas _canvas)
    {
        this.canvas = _canvas;
        this.paint = new Paint();
        this.setFont(AndroidFont.create(Font.Family.SANS_SERIF, Font.Style.PLAIN, (int)this.paint.getTextSize()));
        this.path = new Path();
    }
    
    @Override public void close() { }
    @Override public boolean isReady() { return true; }
    
    // Color
    @Override public int getColor() { return this.paint.getColor(); }
    @Override public void setColor(int color) { this.paint.setColor(color); }
    
    // Font
    // public static Font convertFromTypeface(String typefaceFamily, int typefaceStyle, float typefaceSize)
    // {
        // Font.Style fontStyle = AndroidDrawingContext.convertFromTypefaceStyle(typefaceStyle);
        // return new Font(typefaceFamily, fontStyle, (int)typefaceSize);
    // }
    // public static Typeface convertToTypeface(Font font)
    // {
        // int typefaceStyle = AndroidDrawingContext.convertToTypefaceStyle(font.style);
        // return Typeface.create(font.family, typefaceStyle);
    // }
    @Override public Font getFont() { return this.font; }
    // {
        // Typeface typeface = this.paint.getTypeface();
        // int typefaceStyle = (typeface == null) ? 0 : typeface.getStyle();
        // float typefaceSize = this.paint.getTextSize();
        // return AndroidDrawingContext.convertFromTypeface(this.typefaceFamily, typefaceStyle, typefaceSize); 
    // }
    @Override public void setFont(Font _font) 
    {
        // this.typefaceFamily = font.family;
        // this.paint.setTypeface(AndroidDrawingContext.convertToTypeface(font)); 
        this.font = _font;
        Typeface typeface = ((AndroidFont)this.font).getTypeface();
        this.paint.setTypeface(typeface);
        this.paint.setTextSize(this.font.getSize()); 
    }
    
    // Font Layout
    @Override public FontLayout getFontLayout(Font font) { return new AndroidFontLayout(font); }
    
    // Transforms
    @Override public void scale(float sx, float sy) { this.canvas.scale(sx, sy); }
    
    // Image
    @Override public void drawImage(DrawingImage drawingImage, int x, int y)
    {
        AndroidDrawingImage androidDrawingImage = (AndroidDrawingImage)drawingImage;
        this.canvas.drawBitmap(androidDrawingImage.image, x, y, this.paint);
    }
    
    // Oval
    @Override public void drawOval(int x, int y, int width, int height) 
    {
        this.paint.setStyle(Paint.Style.STROKE); 
        this.canvas.drawOval(x, y, x + width, y + height, this.paint); 
    }
    @Override public void fillOval(int x, int y, int width, int height) 
    {
        this.paint.setStyle(Paint.Style.FILL); 
        this.canvas.drawOval(x, y, x + width, y + height, this.paint); 
    }
    
    // Polygon
    public static Path setPathFromPolygon(Path path, Polygon polygon)
    {
        path.rewind();
        int size = polygon.points.size();
        if (size == 0) { return path; }
        FloatPoint2D p0 = polygon.points.get(0);
        path.moveTo(p0.x, p0.y);
        for (int i = 1; i < size; ++i)
        {
            FloatPoint2D p = polygon.points.get(i);
            path.lineTo(p.x, p.y);
        }
        path.close();
        return path;
    }
    @Override public void drawPolygon(Polygon polygon) 
    {
        this.setPathFromPolygon(this.path, polygon);
        this.paint.setStyle(Paint.Style.STROKE); 
        this.canvas.drawPath(this.path, this.paint); 
    }
    @Override public void fillPolygon(Polygon polygon) 
    {
        this.setPathFromPolygon(this.path, polygon);
        this.paint.setStyle(Paint.Style.FILL); 
        this.canvas.drawPath(this.path, this.paint); 
    }
    
    // Rectangle
    @Override public void drawRect(int x, int y, int width, int height) 
    {
        this.paint.setStyle(Paint.Style.STROKE); 
        this.canvas.drawRect(x, y, x + width, y + height, this.paint);
    }
    @Override public void fillRect(int x, int y, int width, int height) 
    {
        this.paint.setStyle(Paint.Style.FILL); 
        this.canvas.drawRect(x, y, x + width, y + height, this.paint); 
    }
    
    // String
    @Override public void drawString(String text, int x, int y) 
    {
        this.paint.setStyle(Paint.Style.FILL); 
        this.canvas.drawText(text, x, y, this.paint); 
    }
}