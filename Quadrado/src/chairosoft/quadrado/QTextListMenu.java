/* 
 * Nicholas Saney 
 * 
 * Created: April 25, 2015
 * Modified: May 04, 2015
 * 
 * QTextListMenu.java
 * QTextListMenu abstract class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.Polygon;
import chairosoft.ui.graphics.Color;
import chairosoft.ui.graphics.DrawingContext;
import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.Font;
import chairosoft.ui.graphics.FontLayout;
import chairosoft.ui.graphics.WrappedText;

import java.util.ArrayDeque;
import java.util.ArrayList;
// import java.util.List;
// import java.util.Collections;


/**
 * A listed selectable menu whose menu items are simple text.
 */
public class QTextListMenu extends QListMenu
{
    //
    // Constants
    //
    
    public static final int AUTO_WIDTH = 0;
    
    
    //
	// Instance Variables
	//
	
    protected ArrayList<String> textLines;
    protected DrawingImage outerImage = null;
    private FontLayout fontLayout = null;
    private int pointerWidth = 0;
    private int pointerSpaceWidth = 0;
    private int pointerHeight = 0;
    private int pointerColor = Color.BLACK;
    private int width = -1;
    private int height = -1;
    private QScrollAnimator scrollAnimator = new QScrollAnimator();
    private int textColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    private int borderWidth = 1; 
    private int borderColor = Color.BLACK;
    
    
	
	//
	// Constructor
	//
    
    public QTextListMenu(String _name, MenuItem... _menuItems)
    {
        super(_name, _menuItems);
    }
    
    public QTextListMenu(String _name, FontLayout _fontLayout, int x, int y, int w, int h, MenuItem... _menuItems)
    {
        super(_name, _menuItems);
        this.x = x;
        this.y = y;
        this.setup(_fontLayout, w, h);
    }
	
	
	//
	// Instance Methods 
	//
    
    public final void setup(FontLayout _fontLayout, int _height)
    {
        int _width = this.getSuggestedWidth(_fontLayout);
        this.setup(_fontLayout, _width, _height);
    }
    
    public final void setup(FontLayout _fontLayout, int _width, int _height)
    {
        this.setFontLayout(null);
        this.setWidth(_width);
        this.setHeight(_height);
        this.setFontLayout(_fontLayout);
    }
    
    protected final void configure() { if (this.canConfigure()) { this.doConfigure(); } }
    protected boolean canConfigure() { return this.fontLayout != null; }
    protected void doConfigure()
    {
        int listTextWidth = this.getWidthOfLongestMenuItem(this.fontLayout);
        
        int emWidth = this.fontLayout.widthOf("M");
        this.pointerWidth = emWidth;
        this.pointerSpaceWidth = emWidth / 2;
        this.pointerHeight = this.fontLayout.ascent() / 2;
        this.pointerHeight = this.pointerHeight - (this.pointerHeight % 2) + 1;
        
        int listWidth = this.pointerSpaceWidth + this.pointerWidth + this.pointerSpaceWidth + listTextWidth;
        int listHeight = this.readonlyMenuItems.size() * this.fontLayout.height();
        
        this.outerImage = DrawingImage.create(listWidth, listHeight, DrawingImage.Config.ARGB_8888);
        this.configureOuterImage();
    }
    
    protected final void configureOuterImage() { if (this.canConfigureOuterImage()) { this.doConfigureOuterImage(); } }
    protected boolean canConfigureOuterImage() { return this.canConfigure() && this.outerImage != null; }
    protected void doConfigureOuterImage()
    {
        try (DrawingContext outerImageContext = this.outerImage.getContext())
        {
            // background color
            outerImageContext.setColor(this.backgroundColor);
            outerImageContext.fillRect(0, 0, this.outerImage.getWidth(), this.outerImage.getHeight());
            
            // pointer
            outerImageContext.setColor(this.pointerColor);
            int x1 = this.pointerSpaceWidth;
            int x2 = this.pointerSpaceWidth + this.pointerWidth;
            int pointerOffsetY = ((this.getSelectionIndex() + 1) * this.fontLayout.height()) - this.fontLayout.descent();
            int y1 = pointerOffsetY - this.pointerHeight;
            int y2 = pointerOffsetY - (this.pointerHeight / 2) - 1;
            int y3 = pointerOffsetY;
            outerImageContext.fillPolygon(new Polygon(new FloatPoint2D(x1, y1), new FloatPoint2D(x1, y3), new FloatPoint2D(x2, y2)));
            
            // text lines
            outerImageContext.setColor(this.textColor);
            outerImageContext.setFont(this.getFontLayout().font);
            int textOffsetX = this.pointerSpaceWidth + this.pointerWidth + this.pointerSpaceWidth;
            outerImageContext.drawLinesOfText(this.textLines, textOffsetX, 0);
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
    protected boolean canConfigureImage() 
    {
        return this.canConfigureOuterImage()
            && (this.width > 0 || this.width == AUTO_WIDTH) 
            && this.height > 0;
    }
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
    
    @Override
    public void close(int parentsToClose)
    {
        super.close(parentsToClose);
        this.scrollAnimator.clearScrollQueue();
    }
    
    @Override 
    protected void onMenuItemsChanged()
    {
        super.onMenuItemsChanged();
        
        if (this.textLines == null) { this.textLines = new ArrayList<String>(); }
        this.textLines.clear();
        for (MenuItem item : this.readonlyMenuItems) { this.textLines.add(item.name); }
        
        this.configure();
    }
    
    @Override 
    protected void onSelectionChange(int updatedIndex, int oldIndex)
    {
        super.onSelectionChange(updatedIndex, oldIndex);
        this.configureOuterImage(); // TODO: change this to configureImage()
        
        int updatedScrollHeight = updatedIndex * this.fontLayout.height();
        int scrollRangeStart = this.scrollAnimator.getScrollHeight();
        int scrollRangeEnd = scrollRangeStart + this.height;
        if (updatedScrollHeight < scrollRangeStart || scrollRangeEnd <= updatedScrollHeight)
        {
            int scrollHeightChange = (updatedIndex - oldIndex) * this.fontLayout.height();
            this.scrollAnimator.moveScrollHeight(scrollHeightChange, 10);
        }
    }
    
    public FontLayout getFontLayout() { return this.fontLayout; }
    public void setFontLayout(FontLayout fl) { this.fontLayout = fl; this.configure(); }
    
    public int getWidthOfLongestMenuItem(FontLayout fl)
    {
        int listTextWidth = 0;
        for (String line : this.textLines)
        {
            int itemWidth = fl.widthOf(line);
            if (itemWidth > listTextWidth) { listTextWidth = itemWidth; }
        }
        return listTextWidth;
    }
    public int getSuggestedWidth(FontLayout fl)
    {
        return this.getWidthOfLongestMenuItem(fl) + (2 * fl.widthOf("M")) + (2 * this.getBorderWidth());
    }
	public int getWidth() { return this.width; }
    public void setWidth(int w) { this.width = w; this.configureImage(); }
    
    public int getHeight() { return this.height; }
    public void setHeight(int h) { this.height = h; this.configureImage(); }
    
    public void advanceScrollOneClick()
    {
        this.scrollAnimator.pollScrollQueue();
        this.configureImage();
    }
    
    public int getTextColor() { return this.textColor; }
    public void setTextColor(int argb) { this.textColor = argb; this.configureOuterImage(); }
    
    public int getBackgroundColor() { return this.backgroundColor; }
    public void setBackgroundColor(int argb) { this.backgroundColor = argb; this.configureOuterImage(); }
    
    public int getBorderColor() { return this.borderColor; }
    public void setBorderColor(int argb) { this.borderColor = argb; this.configureImage(); }
    
    public int getBorderWidth() { return this.borderWidth; }
    public void setBorderWidth(int bw) { this.borderWidth = bw; this.configureImage(); }
    
    @Override
    public void setTitleVisibility(boolean visibility)
    {
        super.setTitleVisibility(visibility);
        //this.configureImage(); // TODO: allow visible titles
    }
}