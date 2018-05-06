/* 
 * Nicholas Saney 
 * 
 * Created: May 2015
 * 
 * DesktopFont.java
 * DesktopFont class definition
 * 
 */

package chairosoft.quadrado.desktop.graphics;

import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.FontFamily;
import chairosoft.quadrado.ui.graphics.FontStyle;

import java.awt.*;

public class DesktopFontFace extends FontFace {
    
    ////// Instance Properties //////
    protected final Font awtFont;
    public Font getAwtFont() {
        return this.awtFont;
    }
    
    @Override
    public boolean isBold() {
        return this.awtFont.isBold();
    }
    
    @Override
    public boolean isItalic() {
        return this.awtFont.isItalic();
    }
    
    
    ////// Constructors //////
    public DesktopFontFace(String _family, FontStyle _style, int _size) {
        super(_family, _style, _size);
        int awtStyle = DesktopFontStyle.toAwtFontStyle(_style);
        this.awtFont = new Font(_family, awtStyle, _size);
    }
    
    public DesktopFontFace(FontFamily logicalFamily, FontStyle _style, int _size) {
        super(logicalFamily.name, _style, _size);
        String awtFamily = DesktopFontFamily.toAwtFontFamily(logicalFamily);
        int awtStyle = DesktopFontStyle.toAwtFontStyle(_style);
        this.awtFont = new Font(awtFamily, awtStyle, _size);
    }
    
    public DesktopFontFace(Font _awtFont) {
        super(
            _awtFont.getFamily(),
            DesktopFontStyle.fromAwtFontStyle(_awtFont.getStyle()),
            _awtFont.getSize()
        );
        this.awtFont = _awtFont;
    }
    
}