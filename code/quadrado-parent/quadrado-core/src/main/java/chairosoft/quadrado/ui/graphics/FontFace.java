/* 
 * Nicholas Saney 
 * 
 * Created: January 25, 2015
 * 
 * Font.java
 * Font class definition
 * 
 */

package chairosoft.quadrado.ui.graphics;

import chairosoft.quadrado.ui.system.UserInterfaceProvider;

import java.io.IOException;

/**
 * An object representing a particular typeface / font.
 */
public abstract class FontFace {
    
    ////// Instance Properties //////
    protected final String family;
    public String getFamily() {
        return this.family;
    }
    
    protected final FontStyle style;
    public FontStyle getStyle() {
        return this.style;
    }
    
    protected final int size;
    public int getSize() {
        return this.size;
    }
    
    
    ////// Constructors //////
    protected FontFace(String _family, FontStyle _style, int _size) {
        this.family = _family;
        this.style = _style;
        this.size = _size;
    }
    
    
    ////// Instance Methods - Abstract //////
    public abstract boolean isBold();
    public abstract boolean isItalic();
    
    
    ////// Instance Methods - Concrete //////
    public FontFace deriveByStyle(FontStyle derivedStyle) {
        return UserInterfaceProvider.get().createFontFace(this.family, derivedStyle, this.size);
    }
    
    public FontFace deriveBySize(int derivedSize) {
        return UserInterfaceProvider.get().createFontFace(this.family, this.style, derivedSize);
    }
    
    public FontFace derive(FontStyle derivedStyle, int derivedSize) {
        return UserInterfaceProvider.get().createFontFace(this.family, derivedStyle, derivedSize);
    }
    
}