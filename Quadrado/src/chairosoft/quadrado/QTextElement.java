/* 
 * Nicholas Saney 
 * 
 * Created: November 19, 2013
 * 
 * QTextElement.java
 * QTextElement abstract class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.graphics.DrawingContext;
import chairosoft.ui.graphics.FontLayout;

/**
 * A drawable element that handles text. 
 */
public abstract class QTextElement extends QDrawable implements QPositionedDrawable
{
	//
	// Instance Variables
	//
	
    public int x = 0;
    public int y = 0;
    private FontLayout fontLayout = null;
    private String text = "";
	
	
	//
	// Constructor 
	//
	
    public QTextElement()
    {
        // use setters to init
    }
    
    
    // 
    // Instance Methods
    //
    
    protected final void configure() { if (this.canConfigure()) { this.doConfigure(); } }
    protected boolean canConfigure() { return this.fontLayout != null && this.text != null; }
    protected abstract void doConfigure();
    
    public final FontLayout getFontLayout() { return this.fontLayout; }
    public final void setFontLayout(FontLayout fl) { this.fontLayout = fl; this.configure(); }
    
    public final String getText() { return this.text; }
    public final void setText(String str) { this.text = str; this.configure(); }
    
	public final void drawToContextAtOwnPosition(DrawingContext ctx) { this.drawToContext(ctx, this.x, this.y); }
}