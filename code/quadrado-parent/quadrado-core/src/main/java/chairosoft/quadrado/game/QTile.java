/* 
 * Nicholas Saney 
 * 
 * Created: February 11, 2013
 * 
 * QTile.java
 * QTile class definition
 * 
 */

package chairosoft.quadrado.game;

import chairosoft.quadrado.ui.geom.FloatPoint2D;
import chairosoft.quadrado.ui.geom.Rectangle;

import chairosoft.quadrado.ui.graphics.DrawingImage;

import chairosoft.quadrado.util.Loading;
import static chairosoft.quadrado.util.Loading.*;

import java.util.*;


import chairosoft.quadrado.util.Identity;
import nu.xom.*;

public class QTile extends QCollidable implements Comparable<QTile>
{
    //
    // Constants
    //
    
    public static final int CODE_LENGTH = 2;
    
    
    //
    // Static Variables
    //
    
    
    //
    // Static Initialization
    //
    
    // static init now called from ensureQTileMap
    
    
    //
    // Instance Variables
    //
    
    public final String code;
    public final int    imageIndex;
    
    
    //
    // Constructor
    //
    
    protected QTile(String _code, int _imageIndex, DrawingImage _image, Collection<FloatPoint2D> _points) 
    {
        this.code = _code; 
        this.imageIndex = _imageIndex; 
        this.image = _image; 
        if (_points != null) { for (FloatPoint2D p : _points) { this.addPoint((int)p.x, (int)p.y); } }
    }
    
    protected QTile(String _code, int _imageIndex, DrawingImage _image, FloatPoint2D[] _points) 
    { this(_code, _imageIndex, _image, (_points == null ? null : Arrays.asList(_points))); }
    
    protected QTile(QTile qt) 
    { this(qt.code, qt.imageIndex, qt.image, qt.points); }
    
    
    //
    // Instance Methods
    //
    
    public int getWidth() { return QTileset.getTileWidth(); }
    public int getHeight() { return QTileset.getTileHeight(); }
    
    @Override public String toString()
    {
        String result = "[" + this.code + "][" + this.imageIndex + "][";
        if (!this.points.isEmpty())
        {
            for (FloatPoint2D p : this.points) { result += "(" + p.x + "," + p.y + ")"; }
        }
        result += "]";
        return result;
    }
    
    @Override public int compareTo(QTile that)
    {
        return this.code.compareTo(that.code);
    }
    
    
    //
    // Static Methods
    //
    
    
    public static QTile getQTileFromXmlElement(Element xmlQTile, List<? extends DrawingImage> images)
    {
        QTile result = null;
        
        try
        {
            Loading.ensureName(xmlQTile, "qtile");
            
            AttributeValue<String> attrCode = new AttributeValue<>(xmlQTile, "code", Identity.STRING);
            AttributeValue<Integer> attrImage = new AttributeValue<>(xmlQTile, "image", new RangedIntAttributeParser("QTile image", 0, Integer.MAX_VALUE));
            Loading.ensureAllValidAttributes(attrCode, attrImage);
            
            String codeValue = attrCode.getValue();
            if (codeValue.length() != QTile.CODE_LENGTH) 
            { 
                throw new XmlParsingException(String.format("QTile code (%s) is not the correct length (%s).", codeValue, QTile.CODE_LENGTH));
            }
            
            int imageValue = attrImage.getValue();
            DrawingImage tileImage = images.get(imageValue);
            
            final List<FloatPoint2D> pointList = new ArrayList<>();
            
            Loading.applyActionToXmlElementChildren
            (
                xmlQTile,
                child -> QTile.addRectangleOrPointToListFromXmlElement(child, pointList)
            );
            
            result = new QTile(codeValue, imageValue, tileImage, pointList);
        }
        catch (Exception ex)
        {
            System.err.println("[qtile from XML]");
            ex.printStackTrace();
        }
        
        return result;
    }
    
    // private static void addQTileToMapFromXmlElement(Element xmlQTile)
    // {
        // QTile qt = QTile.getQTileFromXmlElement(xmlQTile);
        // QTile.getTileMap().put(qt.getCode(), qt);
    // }
    
    private static void addRectangleOrPointToListFromXmlElement(Element xmlElement, List<FloatPoint2D> pointList)
    {
        switch (xmlElement.getLocalName())
        {
            case "point": addPointToListFromXmlElement(xmlElement, pointList); break;
            case "rectangle": addRectanglePointsToListFromXmlElement(xmlElement, pointList); break;
            default: break;
        }
    }
    
    private static void addPointToListFromXmlElement(Element xmlPoint, List<FloatPoint2D> pointList)
    {
        FloatPoint2D point = Loading.parseFloatPoint2D(xmlPoint, 0, 0, QTileset.getTileWidth(), QTileset.getTileHeight());
        pointList.add(point);
    }
    
    private static void addRectanglePointsToListFromXmlElement(Element xmlRectangle, List<FloatPoint2D> pointList)
    {
        Rectangle r = Loading.parseRectangle(xmlRectangle, 1, 1);
        pointList.add(new FloatPoint2D(r.x,           r.y));
        pointList.add(new FloatPoint2D(r.x + r.width, r.y));
        pointList.add(new FloatPoint2D(r.x + r.width, r.y + r.height));
        pointList.add(new FloatPoint2D(r.x,           r.y + r.height));
    }
        
    // // // public static QTile getNewQTile(final String code)
    // // // {
        // // // QTile result = null;
        
        // // // try
        // // // {
            // // // if (getTileMap().containsKey(code)) 
            // // // {
                // // // result = new QTile(getTileMap().get(code));
            // // // }
            // // // else 
            // // // {        
                // // // QTile.addQTileToMap(code);
                // // // QTile qt = getTileMap().get(code);
                // // // result = (qt == null) ? null : new QTile(qt);
            // // // }
        // // // }
        // // // catch (Exception ex)
        // // // {
            // // // result = null;
        // // // }
        
        // // // return result;
    // // // }
    
    // // // public static void disposeQTile(final String code)
    // // // {
        // // // QTile tile = getTileMap().get(code);
        // // // if (tile == null) { return; }
        
        // // // tile.dispose();
        // // // getTileMap().remove(code);
    // // // }
    
    // public static void disposeAllQTiles()
    // {
        // Set<String> qTileCodes = new HashSet<>(getQTileCodes());
        // for (String code : qTileCodes) { disposeQTile(code); }
    // }
    
    // public static Set<String> getQTileCodes() { return getTileMap().keySet(); }
}