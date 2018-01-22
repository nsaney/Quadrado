/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * AndroidDrawingContext.java
 * AndroidDrawingContext class definition
 * 
 */

package chairosoft.quadrado.android.graphics;

import chairosoft.quadrado.ui.UserInterfaceProvider;
import chairosoft.quadrado.ui.geom.FloatPoint2D;
import chairosoft.quadrado.ui.geom.Polygon;

import chairosoft.quadrado.ui.graphics.*;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

public class AndroidDrawingContext extends DrawingContext
{
    private static final Paint DEFAULT_PAINT = new Paint();
    private static final FontFace DEFAULT_FONT = UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.PLAIN, (int)DEFAULT_PAINT.getTextSize());
    
    public final Canvas canvas;
    protected final Paint paint;
    protected FontFace fontFace;
    protected final Path path;
    public AndroidDrawingContext(Canvas _canvas)
    {
        this.canvas = _canvas;
        this.paint = new Paint(DEFAULT_PAINT);
        this.setFontFace(DEFAULT_FONT);
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
    // public static Typeface convertToTypeface(Font fontFace)
    // {
        // int typefaceStyle = AndroidDrawingContext.convertToTypefaceStyle(fontFace.style);
        // return Typeface.create(fontFace.family, typefaceStyle);
    // }
    @Override public FontFace getFontFace() { return this.fontFace; }
    // {
        // Typeface typeface = this.paint.getTypeface();
        // int typefaceStyle = (typeface == null) ? 0 : typeface.getStyle();
        // float typefaceSize = this.paint.getTextSize();
        // return AndroidDrawingContext.convertFromTypeface(this.typefaceFamily, typefaceStyle, typefaceSize); 
    // }
    public void setFontFace(FontFace _font)
    {
        // this.typefaceFamily = fontFace.family;
        // this.paint.setTypeface(AndroidDrawingContext.convertToTypeface(fontFace));
        this.fontFace = _font;
        Typeface typeface = ((AndroidFontFace)this.fontFace).getTypeface();
        this.paint.setTypeface(typeface);
        this.paint.setTextSize(this.fontFace.getSize());
    }
    
    // Font Layout
    @Override public FontLayout getFontLayout(FontFace _fontFace) { return new AndroidFontLayout(_fontFace); }
    
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