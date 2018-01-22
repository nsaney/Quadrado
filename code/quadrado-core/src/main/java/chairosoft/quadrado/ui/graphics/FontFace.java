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

import chairosoft.quadrado.ui.UserInterfaceProvider;
import chairosoft.quadrado.util.Loading;

import java.io.InputStream;
import java.io.IOException;

/**
 * An object representing a particular typeface / font.
 */
public abstract class FontFace {
    
    ////// Constants //////
    public static final String EMBEDDED_FONT_FOLDER = "font";
    
    
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
    
    
    ////// Static Methods //////
    public static FontFace createFromEmbeddedFont(String fontName) throws IOException {
        String fontLocation = Loading.getPathInFolder(EMBEDDED_FONT_FOLDER, fontName);
        InputStream fontResourceStream = Loading.getInputStreamFromPath(fontLocation);
        return UserInterfaceProvider.get().createFontFace(fontResourceStream, fontName);
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