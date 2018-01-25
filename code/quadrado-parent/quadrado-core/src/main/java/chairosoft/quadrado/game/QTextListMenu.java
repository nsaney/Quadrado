/* 
 * Nicholas Saney 
 * 
 * Created: April 25, 2015
 * 
 * QTextListMenu.java
 * QTextListMenu abstract class definition
 * 
 */

package chairosoft.quadrado.game;

import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.ui.graphics.Color;
import chairosoft.quadrado.ui.graphics.DrawingContext;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.FontLayout;

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
    private int pointerSpaceWidth = 0;
    private int width = -1;
    private int height = -1;
    private QScrollAnimator scrollAnimator = new QScrollAnimator();
    private int textColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    private QBoxStyle boxStyle = null;
    private DrawingImage boxImage = null;
    
    
    //
    // Constructor
    //
    
    public QTextListMenu(String _name, MenuItem... _menuItems)
    {
        super(_name, _menuItems);
    }
    
    public QTextListMenu(String _name, FontLayout _fontLayout, QBoxStyle _boxStyle, int x, int y, int w, int h, MenuItem... _menuItems)
    {
        super(_name, _menuItems);
        this.x = x;
        this.y = y;
        this.setup(_fontLayout, _boxStyle, w, h);
    }
    
    
    //
    // Instance Methods 
    //
    
    public final void setup(FontLayout _fontLayout, QBoxStyle _boxStyle, int _height)
    {
        int _width = this.getSuggestedWidth(_fontLayout, _boxStyle);
        this.setup(_fontLayout, _boxStyle, _width, _height);
    }
    
    public final void setup(FontLayout _fontLayout, QBoxStyle _boxStyle, int _width, int _height)
    {
        this.setFontLayout(null);
        this.setWidth(_width);
        this.setHeight(_height);
        this.setBoxStyle(_boxStyle);
        this.setFontLayout(_fontLayout);
    }
    
    protected final void configure() { if (this.canConfigure()) { this.doConfigure(); } }
    protected boolean canConfigure() { return this.fontLayout != null && this.boxStyle != null; }
    protected void doConfigure()
    {
        int listTextWidth = this.getWidthOfLongestMenuItem(this.fontLayout);
        
        int emWidth = this.fontLayout.widthOf("M");
        this.pointerSpaceWidth = emWidth / 2;
        
        int listWidth = this.pointerSpaceWidth + this.boxStyle.rightArrow.getWidth() + this.pointerSpaceWidth + listTextWidth;
        int listHeight = this.readonlyMenuItems.size() * this.fontLayout.height();
        
        this.outerImage = UserInterfaceProvider.get().createDrawingImage(listWidth, listHeight, DrawingImage.Config.ARGB_8888);
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
            int pointerOffsetX = this.pointerSpaceWidth;
            int pointerOffsetY = (this.getSelectionIndex() * this.fontLayout.height()) 
                               + (((this.fontLayout.ascent() * 3 / 2) - this.boxStyle.rightArrow.getHeight()) / 2)
            ;
            outerImageContext.drawImage(this.boxStyle.rightArrow, pointerOffsetX, pointerOffsetY);
            
            // text lines
            outerImageContext.setColor(this.textColor);
            outerImageContext.setFontFace(this.getFontLayout().fontFace);
            int textOffsetX = this.pointerSpaceWidth + this.boxStyle.rightArrow.getWidth() + this.pointerSpaceWidth;
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
    public int getSuggestedWidth(FontLayout fl, QBoxStyle style)
    {
        return this.getWidthOfLongestMenuItem(fl) + (2 * fl.widthOf("M")) + (style.borderWidths.left + style.borderWidths.right);
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
    
    public final QBoxStyle getBoxStyle() { return this.boxStyle; }
    public final void setBoxStyleCode(String code) { this.setBoxStyle(QBoxStyle.get(code)); }
    public final void setBoxStyle(QBoxStyle style) { this.boxStyle = style; this.boxImage = null; this.configureImage(); }
    
    @Override
    public void setTitleVisibility(boolean visibility)
    {
        super.setTitleVisibility(visibility);
        //this.configureImage(); // TODO: allow visible titles
    }
}