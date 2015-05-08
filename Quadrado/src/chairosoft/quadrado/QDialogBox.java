/* 
 * Nicholas Saney 
 * 
 * Created: November 19, 2013
 * 
 * QDialogBox.java
 * QDialogBox class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.graphics.Color;
import chairosoft.ui.graphics.DrawingContext;
import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.Font;
import chairosoft.ui.graphics.FontLayout;
import chairosoft.ui.graphics.WrappedText;

import java.util.ArrayDeque;

/**
 * A scrollable, resizeable, drawable dialog box.
 */
public class QDialogBox extends QTextElement
{
	//
	// Instance Variables
	//
	
    protected WrappedText wrappedText = null;
    protected DrawingImage outerImage = null;
    private int width = -1;
    private int height = -1;
    private QScrollAnimator scrollAnimator = new QScrollAnimator();
    private int textColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    private int borderWidth = 1; 
    private int borderColor = Color.BLACK;
    // TODO: typewriter effect???
    // TODO: inner padding
    // TODO: offset by font descent (maybe in context?)
    
	
	//
	// Constructor
	//
    
    public QDialogBox()
    {
        // use setters to init
    }
    
    public QDialogBox(FontLayout _fontLayout, String _text, int _width, int _height)
    {
        this.setup(_fontLayout, _text, _width, _height);
    }
	
	
	//
	// Instance Methods 
	//
    
    public final void setup(FontLayout _fontLayout, String _text, int _width, int _height)
    {
        this.setText(null);
        this.setWidth(_width);
        this.setHeight(_height);
        this.setFontLayout(_fontLayout);
        this.setText(_text);
    }
    
    @Override protected boolean canConfigure() { return super.canConfigure() && this.width > 0; }
    @Override protected void doConfigure()
    {
        String str = this.getText();
        FontLayout fl = this.getFontLayout();
        int w = this.width;
        
        this.wrappedText = new WrappedText(str, fl, w);
        int h = this.wrappedText.lines.size() * fl.height();
        
        this.outerImage = DrawingImage.create(w, h, DrawingImage.Config.ARGB_8888);
        this.configureOuterImage();
    }
    
    protected final void configureOuterImage() { if (this.canConfigureOuterImage()) { this.doConfigureOuterImage(); } }
    protected boolean canConfigureOuterImage() { return this.canConfigure() && this.height > 0 && this.wrappedText != null && this.outerImage != null; }
    protected void doConfigureOuterImage()
    {
        try (DrawingContext outerImageContext = this.outerImage.getContext())
        {
            outerImageContext.setColor(this.backgroundColor);
            outerImageContext.fillRect(0, 0, this.outerImage.getWidth(), this.outerImage.getHeight());
            
            outerImageContext.setColor(this.textColor);
            outerImageContext.setFont(this.getFontLayout().font);
            outerImageContext.drawWrappedText(this.wrappedText, 0, 0);
        }
        catch (Exception ex)
        {
            this.outerImage = null;
            ex.printStackTrace();
        }
        
        this.scrollAnimator.setMaxScrollHeight(this.outerImage.getHeight() - this.height);
        this.configureImage();
    }
    
    protected final void configureImage() { if (this.canConfigureImage()) { this.doConfigureImage(); } }
    protected boolean canConfigureImage() { return this.canConfigureOuterImage(); }
    protected void doConfigureImage()
    {
        int bw = this.getBorderWidth();
        int bwx2 = bw * 2;
        int w = bwx2 + this.width;
        int h = bwx2 + this.height;
        if (this.image == null || this.image.getWidth() != w || this.image.getHeight() != h)
        {
            this.image = DrawingImage.create(w, h, DrawingImage.Config.ARGB_8888);
        }
        
        try (DrawingContext imageContext = this.image.getContext())
        {
            imageContext.setColor(this.backgroundColor);
            imageContext.fillRect(0, 0, this.image.getWidth(), this.image.getHeight());
            
            imageContext.setColor(this.borderColor);
            imageContext.drawRect(0, 0, w - 1, h - 1);
            
            int scrollHeight = this.scrollAnimator.getLatestScrollHeight();
            int subImageWidth = Math.min(this.outerImage.getWidth(), this.width);
            DrawingImage subImage = this.outerImage.getImmutableSubimage(0, scrollHeight, subImageWidth, this.height);
            imageContext.drawImage(subImage, bw, bw);
        }
        catch (Exception ex)
        {
            this.image = null;
            ex.printStackTrace();
        }
    }
    
    
    
	public final int getWidth() { return this.width; }
    public final void setWidth(int w) { this.width = w; this.configure(); }
    
    public final int getHeight() { return this.height; }
    public final void setHeight(int h) { this.height = h; this.configureImage(); }
    
    public void moveScrollLines(int dl, int clicks) { this.scrollAnimator.moveScrollHeight(dl * this.getFontLayout().height(), clicks); }
    public void moveScrollHeight(int dh, int clicks) { this.scrollAnimator.moveScrollHeight(dh, clicks); }
    public final void advanceScrollOneClick()
    {
        this.scrollAnimator.pollScrollQueue();
        this.configureImage();
    }
    
    public final int getTextColor() { return this.textColor; }
    public final void setTextColor(int argb) { this.textColor = argb; this.configureOuterImage(); }
    
    public final int getBackgroundColor() { return this.backgroundColor; }
    public final void setBackgroundColor(int argb) { this.backgroundColor = argb; this.configureOuterImage(); }
    
    public final int getBorderColor() { return this.borderColor; }
    public final void setBorderColor(int argb) { this.borderColor = argb; this.configureImage(); }
    
    public final int getBorderWidth() { return this.borderWidth; }
    public final void setBorderWidth(int bw) { this.borderWidth = bw; this.configureImage(); }
}