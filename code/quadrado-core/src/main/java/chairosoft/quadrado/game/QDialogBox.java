/* 
 * Nicholas Saney 
 * 
 * Created: November 19, 2013
 * 
 * QDialogBox.java
 * QDialogBox class definition
 * 
 */

package chairosoft.quadrado.game;

import chairosoft.quadrado.ui.UserInterfaceProvider;
import chairosoft.quadrado.ui.graphics.Color;
import chairosoft.quadrado.ui.graphics.DrawingContext;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.FontLayout;
import chairosoft.quadrado.ui.graphics.WrappedText;

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
    private QBoxStyle boxStyle = null;
    private DrawingImage boxImage = null;
    
    
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
    
    public QDialogBox(FontLayout _fontLayout, String _text, QBoxStyle _boxStyle, int _width, int _height)
    {
        this.setup(_fontLayout, _text, _boxStyle, _width, _height);
    }
    
    
    //
    // Instance Methods 
    //
    
    public final void setup(FontLayout _fontLayout, String _text, QBoxStyle _boxStyle, int _width, int _height)
    {
        this.setText(null);
        this.setWidth(_width);
        this.setHeight(_height);
        this.setBoxStyle(_boxStyle);
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
        
        this.outerImage = UserInterfaceProvider.get().createDrawingImage(w, h, DrawingImage.Config.ARGB_8888);
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
            outerImageContext.setFontFace(this.getFontLayout().fontFace);
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
    protected boolean canConfigureImage() { return this.canConfigureOuterImage() && this.boxStyle != null; }
    protected void doConfigureImage()
    {
        int bwT = this.boxStyle.borderWidths.top;
        int bwR = this.boxStyle.borderWidths.right;
        int bwB = this.boxStyle.borderWidths.bottom;
        int bwL = this.boxStyle.borderWidths.left;
        
        int w = bwL + this.width + bwR;
        int h = bwT + this.height + bwB;
        
        if (this.image == null || this.image.getWidth() != w || this.image.getHeight() != h)
        {
            this.image = UserInterfaceProvider.get().createDrawingImage(w, h, DrawingImage.Config.ARGB_8888);
        }
        
        if (this.boxImage == null || this.boxImage.getWidth() != w || this.boxImage.getHeight() != h)
        {
            this.boxImage = this.boxStyle.getBox(w, h);
        }
        
        try (DrawingContext imageContext = this.image.getContext())
        {
            imageContext.setColor(this.backgroundColor);
            imageContext.fillRect(bwL, bwT, this.width, this.height);
            
            int scrollHeight = this.scrollAnimator.getLatestScrollHeight();
            int subImageWidth = Math.min(this.outerImage.getWidth(), this.width);
            DrawingImage subImage = this.outerImage.getImmutableSubimage(0, scrollHeight, subImageWidth, this.height);
            imageContext.drawImage(subImage, bwL, bwT);
            
            imageContext.drawImage(this.boxImage, 0, 0);
        }
        catch (Exception ex)
        {
            this.image = null;
            ex.printStackTrace();
        }
    }
    
    
    
    public final int getWidth() { return this.width; }
    public final int getFullWidth() { return this.width + (this.boxStyle == null ? 0 : (this.boxStyle.borderWidths.left + this.boxStyle.borderWidths.right)); }
    public final void setWidth(int w) { this.width = w; this.configure(); }
    
    public final int getHeight() { return this.height; }
    public final int getFullHeight() { return this.height + (this.boxStyle == null ? 0 : (this.boxStyle.borderWidths.top + this.boxStyle.borderWidths.bottom)); }
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
    
    public final QBoxStyle getBoxStyle() { return this.boxStyle; }
    public final void setBoxStyleCode(String code) { this.setBoxStyle(QBoxStyle.get(code)); }
    public final void setBoxStyle(QBoxStyle style) { this.boxStyle = style; this.boxImage = null; this.configureImage(); }
}