/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * Color.java
 * Color class definition
 * 
 */

package chairosoft.quadrado.ui.graphics;


/**
 * An object representing a particular sRGB color.
 */
public class Color
{
    private Color() { }
    
    private static final int RANGE_MIN   =   0;
    private static final int RANGE_MAX   = 255;
    private static final int ALPHA_SHIFT =  24;
    private static final int   RED_SHIFT =  16;
    private static final int GREEN_SHIFT =   8;
    private static final int  BLUE_SHIFT =   0;
    
    public static int create(int rgb) { return Color.create(rgb, false); }
    public static int create(int rgba, boolean hasAlpha)
    {
        int alphaBits = ((hasAlpha ? RANGE_MIN : RANGE_MAX) << ALPHA_SHIFT);
        return rgba | alphaBits;
    }
    
    public static int create(int r, int g, int b) { return Color.create(r, g, b, RANGE_MAX); }
    public static int create(int r, int g, int b, int a)
    {
        if      (r < RANGE_MIN) { throw new IllegalArgumentException("R too small"); }
        else if (g < RANGE_MIN) { throw new IllegalArgumentException("G too small"); }
        else if (b < RANGE_MIN) { throw new IllegalArgumentException("B too small"); }
        else if (a < RANGE_MIN) { throw new IllegalArgumentException("A too small"); }
        else if (r > RANGE_MAX) { throw new IllegalArgumentException("R too large"); }
        else if (g > RANGE_MAX) { throw new IllegalArgumentException("G too large"); }
        else if (b > RANGE_MAX) { throw new IllegalArgumentException("B too large"); }
        else if (a > RANGE_MAX) { throw new IllegalArgumentException("A too large"); }
        return (r << RED_SHIFT) | (g << GREEN_SHIFT) | (b << BLUE_SHIFT) | (a << ALPHA_SHIFT);
    }
    
    
    public static final int BLACK       = Color.create(0xff000000, true);
    public static final int BLUE        = Color.create(0xff0000ff, true);
    public static final int CYAN        = Color.create(0xff00ffff, true);
    public static final int DARK_GRAY   = Color.create(0xff444444, true);
    public static final int GRAY        = Color.create(0xff888888, true);
    public static final int GREEN       = Color.create(0xff00ff00, true);
    public static final int LIGHT_GRAY  = Color.create(0xffcccccc, true);
    public static final int MAGENTA     = Color.create(0xffff00ff, true);
    public static final int RED         = Color.create(0xffff0000, true);
    public static final int TRANSPARENT = Color.create(0x00000000, true);
    public static final int WHITE       = Color.create(0xffffffff, true);
    public static final int YELLOW      = Color.create(0xffffff00, true);
    
    /**
     * Colors from colors.css collection (<a href='http://clrs.cc'>clrs.cc</a>).
     */
    public static final class CC
    {
        private CC() { }
        public static final int NAVY    = Color.create(0xff001f3f, true);
        public static final int BLUE    = Color.create(0xff0074d9, true);
        public static final int AQUA    = Color.create(0xff7fdbff, true);
        public static final int TEAL    = Color.create(0xff39cccc, true);
        public static final int OLIVE   = Color.create(0xff3d9970, true);
        public static final int GREEN   = Color.create(0xff2ecc40, true);
        public static final int LIME    = Color.create(0xff01ff70, true);
        public static final int YELLOW  = Color.create(0xffffdc00, true);
        public static final int ORANGE  = Color.create(0xffff851b, true);
        public static final int RED     = Color.create(0xffff4136, true);
        public static final int FUCHSIA = Color.create(0xfff012be, true);
        public static final int PURPLE  = Color.create(0xffb10dc9, true);
        public static final int MAROON  = Color.create(0xff85144b, true);
        public static final int WHITE   = Color.create(0xffffffff, true);
        public static final int SILVER  = Color.create(0xffdddddd, true);
        public static final int GRAY    = Color.create(0xffaaaaaa, true);
        public static final int BLACK   = Color.create(0xff111111, true);
    }
}