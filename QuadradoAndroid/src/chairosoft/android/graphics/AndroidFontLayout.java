/* 
 * Nicholas Saney 
 * 
 * Created: January 29, 2015
 * 
 * AndroidFontLayout.java
 * AndroidFontLayout class definition
 * 
 */

package chairosoft.android.graphics;

import chairosoft.ui.graphics.Font;
import chairosoft.ui.graphics.FontLayout;

import android.graphics.Paint;
import android.graphics.Rect;

public class AndroidFontLayout extends FontLayout
{
    protected final Paint paint;
    public final int height;
    public final Paint.FontMetricsInt fm;
    public AndroidFontLayout(Font _font)
    {
        super(_font);
        this.paint = new Paint();
        this.paint.setTypeface(((AndroidFont)this.font).getTypeface()); 
        this.paint.setTextSize(this.font.getSize()); 
        this.fm = this.paint.getFontMetricsInt();
        this.height = this.fm.leading + this.fm.bottom - this.fm.top;
    }
    
    @Override public int height() { return this.height; }
    @Override public int ascent() { return this.fm.ascent; }
    @Override public int descent() { return this.fm.descent; }
    @Override public int leading() { return this.fm.leading; }
    @Override public int widthOf(String text) { return (int)this.paint.measureText(text); }
}