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
import chairosoft.quadrado.ui.system.UserInterfaceProvider;

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
    
    public void applyTransparencyToPixel(int x, int y, int transparentColor) {
        if ((this.getPixel(x, y) & 0x00FFFFFF) == transparentColor) {
            this.setPixel(x, y, 0);
        }
    }
    
    public DrawingImage getCloneWithTransparency(int transparentColor) {
        int w = this.getWidth();
        int h = this.getHeight();
        DrawingImage result = UserInterfaceProvider.get().createDrawingImage(w, h, Config.ARGB_8888);
        
        try (DrawingContext resultContext = result.getContext()) {
            resultContext.drawImage(this, 0, 0);
            for (int j = 0; j < h; ++j) {
                for (int i = 0; i < w; ++i) {
                    result.applyTransparencyToPixel(i, j, transparentColor);
                }
            }
        }
        catch (Exception ex) {
            System.err.println("DrawingContext error: " + ex);
        }
        
        return result;
    }
    
}