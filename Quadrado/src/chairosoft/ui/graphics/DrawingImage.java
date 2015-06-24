/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DrawingImage.java
 * DrawingImage class definition
 * 
 */

package chairosoft.ui.graphics;

import chairosoft.dependency.Dependencies;

import chairosoft.ui.geom.Rectangle;

import java.io.InputStream;
import java.io.IOException;

public abstract class DrawingImage
{
    public static enum Config
    {
        NO_CONFIG,
        ARGB_8888,
        RGB_565
    }
    
    protected abstract void init(int w, int h, Config c);
    public static DrawingImage create(int w, int h, Config c)
    {
        DrawingImage result = Dependencies.getNew(DrawingImage.class);
        result.init(w, h, c);
        return result;
    }
    
    protected abstract void init(InputStream in) throws IOException;
    public static DrawingImage create(InputStream in) throws IOException
    {
        DrawingImage result = Dependencies.getNew(DrawingImage.class);
        result.init(in);
        return result;
    }
    
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getPixel(int x, int y);
    public abstract void setPixel(int x, int y, int sRGB);
    public abstract DrawingContext getContext();
    
    public abstract DrawingImage getImmutableSubimage(int x, int y, int w, int h);
    public DrawingImage getImmutableSubimage(Rectangle r) { return this.getImmutableSubimage(r.x, r.y, r.width, r.height); }
    
}