/* 
 * Nicholas Saney 
 * 
 * Created: January 26, 2015
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
    
    public int height() { return this.leading() + this.ascent() + this.descent(); }
    public abstract int ascent();
    public abstract int descent();
    public abstract int leading();
    public abstract int widthOf(String text);
    
    public String toString()
    {
        return String.format("FontLayout(%s:h=%s,a=%s,d=%s,l=%s,wM=%s)", this.font.getFamily(), this.height(), this.ascent(), this.descent(), this.leading(), this.widthOf("M"));
    }
}