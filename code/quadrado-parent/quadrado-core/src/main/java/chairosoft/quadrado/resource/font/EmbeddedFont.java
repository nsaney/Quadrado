package chairosoft.quadrado.resource.font;

import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.TrueTypeFontFaceLoader;

import java.io.IOException;

public class EmbeddedFont {
    
    ////// Constants //////
    public static final TrueTypeFontFaceLoader FONT_FACE_LOADER = new TrueTypeFontFaceLoader();
    
    
    ////// Instance Fields //////
    public final String name;
    
    
    ////// Constructor //////
    public EmbeddedFont(String _name) {
        this.name = _name;
    }
    
    
    ////// Instance Methods //////
    public FontFace load() throws IOException {
        return FONT_FACE_LOADER.load(this.name);
    }
    
}
