/* 
 * Nicholas Saney 
 * 
 * Created: January 29, 2015
 * 
 * AndroidFontLayout.java
 * AndroidFontLayout class definition
 * 
 */

package chairosoft.quadrado.android.graphics;

import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.FontLayout;

import android.graphics.Paint;

public class AndroidFontLayout extends FontLayout
{
    protected final Paint paint;
    public final Paint.FontMetricsInt fm;
    public AndroidFontLayout(FontFace _fontFace)
    {
        super(_fontFace);
        this.paint = new Paint();
        this.paint.setTypeface(((AndroidFontFace)this.fontFace).getTypeface());
        this.paint.setTextSize(this.fontFace.getSize());
        this.fm = this.paint.getFontMetricsInt();
    }
    
    // in Android, ascent and top are given as negatives (so they are relative to the baseline)
    // but to make it the same as Desktop, we'll have to negate the value here
    @Override public int ascent() { return -this.fm.top; }
    @Override public int descent() { return this.fm.bottom; }
    @Override public int leading() { return this.fm.leading; }
    @Override public int widthOf(String text) { return (int)this.paint.measureText(text); }
}