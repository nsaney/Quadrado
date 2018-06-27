package chairosoft.quadrado.asset.box_style;

import chairosoft.quadrado.ui.geom.Rectangle;
import chairosoft.quadrado.ui.graphics.DrawingContext;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;

public class QBoxStyle {
    
    ////// Constants //////
    public static final BoxStyleImageLoader IMAGE_LOADER = new BoxStyleImageLoader();
    
    
    ////// Instance Fields //////
    public final int transparencyRgb;
    public final BorderWidths borderWidths;
    public final BorderLengths borderLengths;
    public final DrawingImage upArrow;
    public final DrawingImage rightArrow;
    public final DrawingImage downArrow;
    public final DrawingImage leftArrow;
    public final DrawingImage topLeftCorner;
    public final DrawingImage topBorder;
    public final DrawingImage topRightCorner;
    public final DrawingImage rightBorder;
    public final DrawingImage bottomRightCorner;
    public final DrawingImage bottomBorder;
    public final DrawingImage bottomLeftCorner;
    public final DrawingImage leftBorder;
    
    
    ////// Constructor //////
    protected QBoxStyle(
        int _transparencyRgb,
        BorderWidths _borderWidths,
        BorderLengths _borderLengths,
        Rectangle upArrowRectangle,
        Rectangle rightArrowRectangle,
        Rectangle downArrowRectangle,
        Rectangle leftArrowRectangle
    ) {
        this.transparencyRgb = _transparencyRgb;
        this.borderWidths = _borderWidths;
        this.borderLengths = _borderLengths;
        
        DrawingImage sourceImage = IMAGE_LOADER.loadOrEmpty(this.getClass(), this.transparencyRgb);
        
        this.upArrow = sourceImage.getImmutableSubimage(upArrowRectangle);
        this.rightArrow = sourceImage.getImmutableSubimage(rightArrowRectangle);
        this.downArrow  = sourceImage.getImmutableSubimage(downArrowRectangle);
        this.leftArrow  = sourceImage.getImmutableSubimage(leftArrowRectangle);
        
        int xL = 0;
        int xC = this.borderWidths.left;
        int xR = this.borderWidths.left + this.borderLengths.horizontal;
        int yT = 0;
        int yC = this.borderWidths.top;
        int yB = this.borderWidths.top + this.borderLengths.vertical;
        int hL = this.borderWidths.left;
        int hC = this.borderLengths.horizontal;
        int hR = this.borderWidths.right;
        int vT = this.borderWidths.top;
        int vC = this.borderLengths.vertical;
        int vB = this.borderWidths.bottom;
        
        this.topLeftCorner     = sourceImage.getImmutableSubimage(xL, yT, hL, vT);
        this.topBorder         = sourceImage.getImmutableSubimage(xC, yT, hC, vT);
        this.topRightCorner    = sourceImage.getImmutableSubimage(xR, yT, hR, vT);
        this.rightBorder       = sourceImage.getImmutableSubimage(xR, yC, hR, vC);
        this.bottomRightCorner = sourceImage.getImmutableSubimage(xR, yB, hR, vB);
        this.bottomBorder      = sourceImage.getImmutableSubimage(xC, yB, hC, vB);
        this.bottomLeftCorner  = sourceImage.getImmutableSubimage(xL, yB, hL, vB);
        this.leftBorder        = sourceImage.getImmutableSubimage(xL, yC, hL, vC);
    }
    
    
    ////// Instance Methods //////
    
    public DrawingImage getBox(int width, int height) {
        DrawingImage result = UserInterfaceProvider.get().createDrawingImage(width, height, DrawingImage.Config.ARGB_8888);
        DrawingContext resultCtx = result.getContext();
        
        int xL = 0;
        int xC = this.borderWidths.left;
        int xR = width - this.borderWidths.right;
        int yT = 0;
        int yC = this.borderWidths.top;
        int yB = height - this.borderWidths.bottom;
        int hC = this.borderLengths.horizontal;
        int vC = this.borderLengths.vertical;
        
        for (int x = xC; x < xR; x += hC) {
            resultCtx.drawImage(this.topBorder, x, yT);
            resultCtx.drawImage(this.bottomBorder, x, yB);
        }
        for (int y = yC; y < yB; y += vC) {
            resultCtx.drawImage(this.leftBorder, xL, y);
            resultCtx.drawImage(this.rightBorder, xR, y);
        }
        resultCtx.drawImage(this.topLeftCorner, xL, yT);
        resultCtx.drawImage(this.topRightCorner, xR, yT);
        resultCtx.drawImage(this.bottomLeftCorner, xL, yB);
        resultCtx.drawImage(this.bottomRightCorner, xR, yB);
        
        return result;
    }
    
    
    ////// Static Methods - Declarative Syntax //////
    protected static BorderWidths borderWidths(int top, int right, int bottom, int left) {
        return new BorderWidths(top, right, bottom, left);
    }
    
    protected static BorderLengths borderLengths(int horizontal, int vertical) {
        return new BorderLengths(horizontal, vertical);
    }
    
    protected static Rectangle arrow(int x, int y, int width, int height) {
        return new Rectangle(x, y, width, height);
    }
    
}
