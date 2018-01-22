/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DesktopDrawingImage.java
 * DesktopDrawingImage class definition
 * 
 */

package chairosoft.quadrado.desktop.graphics;

import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.DrawingContext;

import java.awt.image.BufferedImage;

import java.io.InputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DesktopDrawingImage extends DrawingImage {
    
    ////// Instance Fields //////
    public final BufferedImage image;
    
    
    ////// Constructors //////
    private DesktopDrawingImage(BufferedImage _image) {
        this.image = _image;
    }
    
    public DesktopDrawingImage(int w, int h, Config c) {
        int bufferedImageType = DesktopDrawingImage.getBufferedImageType(c);
        this.image = new BufferedImage(w, h, bufferedImageType);
    }
    
    public DesktopDrawingImage(InputStream in) throws IOException {
        this.image = ImageIO.read(in);
    }
    
    
    ////// Static Methods //////
    public static int getBufferedImageType(Config config) {
        switch (config) {
            case ARGB_8888:
                return BufferedImage.TYPE_INT_ARGB;
            case RGB_565:
                return BufferedImage.TYPE_USHORT_565_RGB;
            default:
                throw new IllegalArgumentException("Unable to convert config value: " + config);
        }
    }
    
    
    ////// Instance Methods //////
    @Override
    public int getWidth() {
        return this.image.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.image.getHeight();
    }
    
    @Override
    public int getPixel(int x, int y) {
        return this.image.getRGB(x, y);
    }
    
    @Override
    public void setPixel(int x, int y, int sRGB) {
        this.image.setRGB(x, y, sRGB);
    }
    
    @Override
    public DrawingContext getContext() {
        return new DesktopDrawingContext(this.image.createGraphics());
    }
    
    @Override
    public DrawingImage getImmutableSubimage(int x, int y, int w, int h) {
        BufferedImage subImage = this.image.getSubimage(x, y, w, h);
        return new DesktopDrawingImage(subImage);
    }
}