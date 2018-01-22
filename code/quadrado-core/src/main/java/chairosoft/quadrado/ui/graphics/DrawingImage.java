/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DrawingImage.java
 * DrawingImage class definition
 * 
 */

package chairosoft.quadrado.ui.graphics;

import chairosoft.quadrado.ui.geom.Rectangle;

public abstract class DrawingImage {
    
    ////// Static Inner Classes //////
    public static enum Config {
        NO_CONFIG,
        ARGB_8888,
        RGB_565
    }
    
    
    ////// Constructor //////
    protected DrawingImage() {
        // nothing here right now
    }
    
    
    ////// Instance Methods - Abstract //////
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getPixel(int x, int y);
    public abstract void setPixel(int x, int y, int sRGB);
    public abstract DrawingContext getContext();
    public abstract DrawingImage getImmutableSubimage(int x, int y, int w, int h);
    
    
    ////// Instance Methods - Concrete //////
    public DrawingImage getImmutableSubimage(Rectangle r) {
        return this.getImmutableSubimage(r.x, r.y, r.width, r.height);
    }
    
}