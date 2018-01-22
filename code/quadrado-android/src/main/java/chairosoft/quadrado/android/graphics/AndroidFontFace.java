/* 
 * Nicholas Saney 
 * 
 * Created: May 31, 2015
 * 
 * AndroidFont.java
 * AndroidFont class definition
 * 
 */

package chairosoft.quadrado.android.graphics;

import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.FontFamily;
import chairosoft.quadrado.ui.graphics.FontStyle;
import chairosoft.quadrado.util.Loading;

import android.graphics.Typeface;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class AndroidFontFace extends FontFace {
    
    ////// Instance Properties //////
    protected final Typeface typeface;
    public Typeface getTypeface() {
        return this.typeface;
    }
    
    @Override
    public boolean isBold() {
        return this.typeface.isBold();
    }
    
    @Override
    public boolean isItalic() {
        return this.typeface.isItalic();
    }
    
    
    ////// Constructors //////
    public AndroidFontFace(String _family, FontStyle _style, int _size) {
        super(_family, _style, _size);
        int typefaceStyle = AndroidFontStyle.toTypefaceStyle(_style);
        this.typeface = Typeface.create(_family, typefaceStyle);
    }
    
    public AndroidFontFace(FontFamily logicalFamily, FontStyle _style, int _size) {
        super(logicalFamily.name, _style, _size);
        Typeface typeface = AndroidFontFamily.toAndroidTypeface(logicalFamily);
        int typefaceStyle = AndroidFontStyle.toTypefaceStyle(_style);
        this.typeface = Typeface.create(typeface, typefaceStyle);
    }
    
    public AndroidFontFace(Typeface _typeface) {
        super(
            AndroidFontFamily.fromAndroidTypeface(_typeface).name,
            AndroidFontStyle.fromTypefaceStyle(_typeface.getStyle()),
            1
        );
        this.typeface = _typeface;
    }
    
}