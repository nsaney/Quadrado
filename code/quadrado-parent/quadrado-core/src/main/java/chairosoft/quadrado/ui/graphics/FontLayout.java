/* 
 * Nicholas Saney 
 * 
 * Created: January 26, 2015
 * 
 * FontLayout.java
 * FontLayout class definition
 * 
 */

package chairosoft.quadrado.ui.graphics;

/**
 * An object with layout information for a particular FontFace.
 */
public abstract class FontLayout
{
    public final FontFace fontFace;
    
    protected FontLayout(FontFace _fontFace)
    {
        this.fontFace = _fontFace;
    }
    
    public int height() { return this.leading() + this.ascent() + this.descent(); }
    public abstract int ascent();
    public abstract int descent();
    public abstract int leading();
    public abstract int widthOf(String text);
    
    public String toString()
    {
        return String.format("FontLayout(%s:h=%s,a=%s,d=%s,l=%s,wM=%s)", this.fontFace.getFamily(), this.height(), this.ascent(), this.descent(), this.leading(), this.widthOf("M"));
    }
}