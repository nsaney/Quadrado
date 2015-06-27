/* 
 * Nicholas Saney 
 * 
 * Created: June 22, 2015
 * 
 * QBoxStyle.java
 * QBoxStyle class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.Rectangle;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import chairosoft.util.function.*;
import chairosoft.util.Loading;
import static chairosoft.util.Loading.*;

import java.io.*; 
import java.util.*;

import nu.xom.*;

/**
 * A style for dialog and menu boxes.
 */
public class QBoxStyle
{
    // 
    // Constants
    // 
    
    public static final String SPEC_FOLDER          = "img";
    public static final String QBOXSTYLES_FILENAME  = "qboxstyles.xml";
    public static final String QBOXSTYLES_FULL_PATH = Loading.getPathInFolder(SPEC_FOLDER, QBOXSTYLES_FILENAME);
    
    
    // 
    // Static Variables
    // 
    
    protected static String                 imageFolder;
    protected static Map<String, QBoxStyle> boxStyleMap;
    
    
    // 
    // Static Initialization
    //
    
    static
    {
        try
        {
            Document doc = Loading.getXmlDocument(QBOXSTYLES_FULL_PATH);
            QBoxStyle.initializeFromXmlDocument(doc);
        }
        catch (Exception ex)
        {
            System.err.println("[qboxstyles file]");
            ex.printStackTrace();
        }
    }
    
    /**
     * Helper for static initialization.
     * @param xmlDocument the XML document from which to initialize the QBoxStyle class.
     */
    private final static void initializeFromXmlDocument(Document xmlDocument)
    {
        Element root = xmlDocument.getRootElement();
        Loading.ensureName(root, "qboxstyles");
        
        AttributeValue<String> attrFolder = new AttributeValue<>(root, "folder", new Identity<String>());
        
        Loading.ensureAllValidAttributes(attrFolder);
        
        QBoxStyle.imageFolder = Loading.getPathInFolder(QBoxStyle.SPEC_FOLDER, attrFolder.getValue());
        QBoxStyle.boxStyleMap = new HashMap<>(); // this is filled in as needed by QBoxStyle.load()
    }
    
    
    // 
    // Instance Variables
    // 
    
    public final String        code;
    public final String        imageLocation;
    public final int           transparentValue;
    public final BorderWidths  borderWidths;
    public final BorderLengths borderLengths;
    public final DrawingImage  upArrow;
    public final DrawingImage  rightArrow;
    public final DrawingImage  downArrow;
    public final DrawingImage  leftArrow;
    protected DrawingImage     topLeftCorner;
    protected DrawingImage     topBorder;
    protected DrawingImage     topRightCorner;
    protected DrawingImage     rightBorder;
    protected DrawingImage     bottomRightCorner;
    protected DrawingImage     bottomBorder;
    protected DrawingImage     bottomLeftCorner;
    protected DrawingImage     leftBorder;
    
    
    // 
    // Constructors
    // 
    
    /**
     * Constructs a QBoxStyle from the given XML element.
     * @param xmlQBoxStyle the XML element to parse
     */
    protected QBoxStyle(Element xmlQBoxStyle)
    {
        String _code = null;
        String _imageLocation = null;
        int _transparentValue = -1;
        BorderWidths _borderWidths = null;
        BorderLengths _borderLengths = null;
        
        Rectangle upArrowRectangle = null;
        Rectangle rightArrowRectangle = null;
        Rectangle downArrowRectangle = null;
        Rectangle leftArrowRectangle = null;
        
        try
        {
            Loading.ensureName(xmlQBoxStyle, "qboxstyle");
            
            AttributeValue<String> attrCode = new AttributeValue<String>(xmlQBoxStyle, "code", new Identity<String>());
            AttributeValue<String> attrImage = new AttributeValue<String>(xmlQBoxStyle, "image", new Identity<String>());
            AttributeValue<Integer> attrTransparent = new AttributeValue<>(xmlQBoxStyle, "transparent", Loading.TRANSPARENCY_DECODER_FUNCTION); 
            Loading.ensureAllValidAttributes(attrCode, attrImage, attrTransparent);
            
            _code = attrCode.getValue();
            _imageLocation = attrImage.getValue();
            _transparentValue = attrTransparent.getValue();
            
            Element xmlBorderWidths = xmlQBoxStyle.getFirstChildElement("border-widths");
            _borderWidths = BorderWidths.parse(xmlBorderWidths);
            
            Element xmlBorderLengths = xmlQBoxStyle.getFirstChildElement("border-lengths");
            _borderLengths = BorderLengths.parse(xmlBorderLengths);
            
            Element xmlUpArrow = xmlQBoxStyle.getFirstChildElement("up-arrow");
            Element xmlUpArrowRectangle = xmlUpArrow.getFirstChildElement("rectangle");
            upArrowRectangle = Loading.parseRectangle(xmlUpArrowRectangle, 1, 1);
            
            Element xmlRightArrow = xmlQBoxStyle.getFirstChildElement("right-arrow");
            Element xmlRightArrowRectangle = xmlUpArrow.getFirstChildElement("rectangle");
            rightArrowRectangle = Loading.parseRectangle(xmlUpArrowRectangle, 1, 1);
            
            Element xmlDownArrow = xmlQBoxStyle.getFirstChildElement("bottom-arrow");
            Element xmlDownArrowRectangle = xmlUpArrow.getFirstChildElement("rectangle");
            downArrowRectangle = Loading.parseRectangle(xmlUpArrowRectangle, 1, 1);
            
            Element xmlLeftArrow = xmlQBoxStyle.getFirstChildElement("left-arrow");
            Element xmlLeftArrowRectangle = xmlUpArrow.getFirstChildElement("rectangle");
            leftArrowRectangle = Loading.parseRectangle(xmlUpArrowRectangle, 1, 1);
            
        }
        catch (Exception ex)
        {
            System.err.println("[qboxstyle]");
            ex.printStackTrace();
        }
        
        this.code = _code;
        this.imageLocation = _imageLocation;
        this.transparentValue = _transparentValue;
        this.borderWidths = _borderWidths;
        this.borderLengths = _borderLengths;
        
        String imagePath = Loading.getPathInFolder(QBoxStyle.imageFolder, this.imageLocation);
        DrawingImage sourceImageRaw = Loading.getImage(imagePath);
        DrawingImage sourceImage = Loading.applyTransparency(sourceImageRaw, this.transparentValue);
        
        this.upArrow    = sourceImage.getImmutableSubimage(upArrowRectangle);
        this.rightArrow = sourceImage.getImmutableSubimage(rightArrowRectangle);
        this.downArrow  = sourceImage.getImmutableSubimage(downArrowRectangle);
        this.leftArrow  = sourceImage.getImmutableSubimage(leftArrowRectangle);
        
        int xL = 0;
        int xC = this.borderWidths.left;
        int xR = this.borderWidths.left + this.borderLengths.horizontal;
        int yT = 0;
        int yC = this.borderWidths.top;
        int yB = this.borderWidths.top + this.borderLengths.vertical;
        
        this.topLeftCorner     = sourceImage.getImmutableSubimage(xL, yT, this.borderWidths.left,        this.borderWidths.top);
        this.topBorder         = sourceImage.getImmutableSubimage(xC, yT, this.borderLengths.horizontal, this.borderWidths.top);
        this.topRightCorner    = sourceImage.getImmutableSubimage(xR, yT, this.borderWidths.right,       this.borderWidths.top);
        this.rightBorder       = sourceImage.getImmutableSubimage(xR, yC, this.borderWidths.right,       this.borderLengths.vertical);
        this.bottomRightCorner = sourceImage.getImmutableSubimage(xR, yB, this.borderWidths.right,       this.borderWidths.bottom);
        this.bottomBorder      = sourceImage.getImmutableSubimage(xC, yB, this.borderLengths.horizontal, this.borderWidths.bottom);
        this.bottomLeftCorner  = sourceImage.getImmutableSubimage(xL, yB, this.borderWidths.left,        this.borderWidths.bottom);
        this.leftBorder        = sourceImage.getImmutableSubimage(xL, yC, this.borderWidths.left,        this.borderLengths.vertical);
    }
    
    
    // 
    // Instance Methods
    // 
    
    public DrawingImage getBox(int width, int height)
    {
        DrawingImage result = DrawingImage.create(width, height, DrawingImage.Config.ARGB_8888);
        DrawingContext resultCtx = result.getContext();
        
        int xL = 0;
        int xC = this.borderWidths.left;
        int xR = width - this.borderWidths.right;
        int yT = 0;
        int yC = this.borderWidths.top;
        int yB = height - this.borderWidths.bottom;
        
        for (int x = xC; x < xR; x += this.borderLengths.horizontal)
        {
            resultCtx.drawImage(this.topBorder, x, yT);
            resultCtx.drawImage(this.bottomBorder, x, yB);
        }
        for (int y = yC; y < yB; y += this.borderLengths.vertical)
        {
            resultCtx.drawImage(this.leftBorder, xL, y);
            resultCtx.drawImage(this.rightBorder, xR, y);
        }
        
        resultCtx.drawImage(this.topLeftCorner, xL, yT);
        resultCtx.drawImage(this.topRightCorner, xR, yT);
        resultCtx.drawImage(this.bottomLeftCorner, xL, yB);
        resultCtx.drawImage(this.bottomRightCorner, xR, yB);
        
        return result;
    }
    
    
    /**
     * Disposes this QBoxStyle.
     */
    public void dispose()
    {
        // nothing here now
    }
    
    
    // 
    // Static Methods
    // 
    
    /** 
     * Gets the QBoxStyle associated with the given code. If there is not currently an 
     * associated box style, then it will be loaded from XML. If it is not specified in 
     * the XML, a runtime exception will be thrown.
     * @param code the code of the QBoxStyle to get or to load and get
     * @return The QBoxStyle associated with the given code.
     */
    public static QBoxStyle get(String code)
    {
        if (!QBoxStyle.boxStyleMap.containsKey(code))
        {
            QBoxStyle.boxStyleMap.put(code, QBoxStyle.load(code));
        }
        return QBoxStyle.boxStyleMap.get(code);
    }
    
    /**
     * Removes and disposes the QBoxStyle associated with the given code. Does nothing 
     * if there is not currently an associated box style.
     * @param code the code of the QBoxStyle to remove and dispose
     */
    public static void disposeAndRemove(String code)
    {
        if (QBoxStyle.boxStyleMap.containsKey(code))
        {
            QBoxStyle boxStyle = QBoxStyle.boxStyleMap.remove(code);
            if (boxStyle != null) { boxStyle.dispose(); }
        }
    }
    
    /**
     * Loads the QBoxStyle associated with the given code.
     * @param code the code of the QBoxStyle to load
     * @return The QBoxStyle specified by the given code.
     */
    protected static QBoxStyle load(final String code)
    {
        QBoxStyle result = null;
        try
        {
            Document doc = Loading.getXmlDocument(QBOXSTYLES_FULL_PATH);
            Element root = doc.getRootElement();
            Loading.ensureName(root, "qboxstyles");
            
            result = Loading.applyFunctionToFirstMatchingXmlElementChild
            (
                root,
                new Loading.PredicateMatchingElementByAttribute("code", code),
                new Function<Element, QBoxStyle>() { public QBoxStyle apply(Element child) { return new QBoxStyle(child); } }
                // child -> new QBoxStyle(child)
            );
        }
        catch (Exception ex)
        {
            System.err.println("[qboxstyles file]");
            ex.printStackTrace();
        }
        return result;
    }
    
    
    //
    // Static Inner Classes 
    // 
    
    public static class BorderWidths
    {
        public final int top;
        public final int right;
        public final int bottom ;
        public final int left;
        
        public BorderWidths(int t, int r, int b, int l)
        {
            this.top = t;
            this.right = r;
            this.bottom = b;
            this.left = l;
        }
        
        public static BorderWidths parse(Element xmlBorderWidths)
        {
            Loading.ensureName(xmlBorderWidths, "border-widths");
            
            int min = 0;
            int max = Integer.MAX_VALUE;
            
            AttributeValue<Integer> attrTop = new AttributeValue<>(xmlBorderWidths, "top", new Loading.RangedIntAttributeParser("top", min, max));
            AttributeValue<Integer> attrRight = new AttributeValue<>(xmlBorderWidths, "right", new Loading.RangedIntAttributeParser("right", min, max));
            AttributeValue<Integer> attrBottom = new AttributeValue<>(xmlBorderWidths, "bottom", new Loading.RangedIntAttributeParser("bottom", min, max));
            AttributeValue<Integer> attrLeft = new AttributeValue<>(xmlBorderWidths, "left", new Loading.RangedIntAttributeParser("left", min, max)); 
            Loading.ensureAllValidAttributes(attrTop, attrRight, attrBottom, attrLeft);
            
            BorderWidths result = new BorderWidths(attrTop.getValue(), attrRight.getValue(), attrBottom.getValue(), attrLeft.getValue());
            return result;
        }
    }
    
    public static class BorderLengths
    {
        public final int horizontal;
        public final int vertical;
        
        public BorderLengths(int h, int v)
        {
            this.horizontal = h;
            this.vertical = v;
        }
        
        public static BorderLengths parse(Element xmlBorderLengths)
        {
            Loading.ensureName(xmlBorderLengths, "border-lengths");
            
            int min = 0;
            int max = Integer.MAX_VALUE;
            
            AttributeValue<Integer> attrHorizontal = new AttributeValue<>(xmlBorderLengths, "horizontal", new Loading.RangedIntAttributeParser("horizontal", min, max));
            AttributeValue<Integer> attrVertical = new AttributeValue<>(xmlBorderLengths, "vertical", new Loading.RangedIntAttributeParser("vertical", min, max));
            Loading.ensureAllValidAttributes(attrHorizontal, attrVertical);
            
            BorderLengths result = new BorderLengths(attrHorizontal.getValue(), attrVertical.getValue());
            return result;
        }
    }
}