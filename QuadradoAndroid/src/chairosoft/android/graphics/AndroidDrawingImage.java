/* 
 * Nicholas Saney 
 * 
 * Created: January 28, 2015
 * 
 * AndroidDrawingImage.java
 * AndroidDrawingImage class definition
 * 
 */

package chairosoft.android.graphics;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.InputStream;
import java.io.IOException;

public class AndroidDrawingImage extends DrawingImage
{
    protected Bitmap image = null;
    public Bitmap getImage() { return this.image; }
    
    protected static Bitmap.Config getBitmapConfig(Config config)
    {
        switch (config)
        {
            case ARGB_8888: return Bitmap.Config.ARGB_8888;
            case RGB_565: return Bitmap.Config.RGB_565;
            default: throw new IllegalArgumentException("Unable to convert config value: " + config);
        }
    }
    
    @Override protected void init(int w, int h, Config c)
    {
        Bitmap.Config bitmapConfig = AndroidDrawingImage.getBitmapConfig(c);
        this.image = Bitmap.createBitmap(w, h, bitmapConfig);
    }
    
    @Override protected void init(InputStream in) throws IOException
    {
        this.image = BitmapFactory.decodeStream(in);
    }
    
    @Override public int getWidth() { return this.image.getWidth(); }
    @Override public int getHeight() { return this.image.getHeight(); }
    @Override public int getPixel(int x, int y) { return this.image.getPixel(x, y); }
    @Override public void setPixel(int x, int y, int sRGB) { this.image.setPixel(x, y, sRGB); }
    @Override public DrawingContext getContext() { return new AndroidDrawingContext(new Canvas(this.image)); }
    @Override public DrawingImage getImmutableSubimage(int x, int y, int w, int h)
    {
        AndroidDrawingImage result = new AndroidDrawingImage();
        result.image = Bitmap.createBitmap(this.image, x, y, w, h); 
        return result;
    }
}