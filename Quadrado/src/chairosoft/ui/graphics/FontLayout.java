/* 
 * Nicholas Saney 
 * 
 * Created: January 26, 2015
 * Modified: January 26, 2015
 * 
 * FontLayout.java
 * FontLayout class definition
 * 
 */

package chairosoft.ui.graphics;

/**
 * An object with layout information for a particular Font.
 */
public abstract class FontLayout
{
    public final Font font;
    
    protected FontLayout(Font _font)
    {
        this.font = _font;
    }
    
    public abstract int height();
    public abstract int ascent();
    public abstract int descent();
    public abstract int leading();
    public abstract int widthOf(String text);
}