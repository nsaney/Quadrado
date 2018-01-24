/* 
 * Nicholas Saney 
 * 
 * Created: February 11, 2013
 * 
 * QMapRoom.java
 * QMapRoom class definition
 * 
 */

package chairosoft.quadrado.game;

import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.ui.geom.IntPoint2D;

import chairosoft.quadrado.ui.graphics.Color;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.DrawingContext;

import chairosoft.quadrado.util.Loading;
import static chairosoft.quadrado.util.Loading.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import chairosoft.quadrado.util.Identity;
import nu.xom.*;

public class QMapRoom extends QAbstractMapRoom
{
    //
    // Constants
    //
    
    public static final String MAP_DIR = "map";
    public static final int    Q_SPACE = 3;
    
    
    //
    // Instance Variables 
    // 
    
    protected final String  code;
    protected int           backgroundColor;
    protected QTileset      qtileset;
    protected QTile[][]     qtileLayout;
    protected Set<String>   qtileCodes = new HashSet<>();
    protected int           width;
    protected int           height;
    protected List<MapLink> mapLinks;
    
    
    //
    // Constructor
    // 
    
    public QMapRoom(String _code) 
    {
        this(
            _code, 
            Loading.getPathInFolder(MAP_DIR, _code + ".xml"), 
            true
        );
    }
    
    protected QMapRoom(String _code, String _absolutePath, boolean _usingInternal)
    {
        this.code = _code;
        try
        {
            Document doc = Loading.getXmlDocument(_absolutePath, _usingInternal);
            this.setPropertiesFromXmlDocument(doc);
            this.setImageFromLayout();
            //System.err.println(this);
            //System.err.printf("Map %s loaded. [width = %s, height = %s]\n", this.code, this.width, this.height);
        }
        catch (RuntimeException rx)
        {
            throw rx;
        }
        catch (Exception ex)
        {
            System.err.println("[qmaproom constructor]");
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    
    //
    // Instance Methods 
    //
    
    //// Get Methods ////
    
    public int getBackgroundColor() { return this.backgroundColor; }
    
    public int getWidthTiles() { return this.width; }
    public int getWidthPixels() { return this.width * QTileset.getTileWidth(); }
    
    public int getHeightTiles() { return this.height; }
    public int getHeightPixels() { return this.height * QTileset.getTileHeight(); }
    
    public Iterable<String> getXmlLinesFromQTileLayout()
    {
        ArrayList<String> result = new ArrayList<>();
        
        result.add("<?xml version='1.0'?>");
        result.add(String.format("<qmaproom background='#%06x'>", this.backgroundColor & 0x00ffffff));
        
        result.add("<layout>");
        for (int row = 0; row < this.qtileLayout.length; ++row)
        {
            String line = "";
            for (int col = 0; col < this.qtileLayout[row].length; ++col)
            {
                line += this.qtileLayout[row][col].code;
            }
            result.add(line);
        }
        result.add("</layout>");
        
        result.add("<links>");
        for (MapLink link : this.mapLinks)
        {
            result.add("\t" + link.toString());
        }
        result.add("</links>");
        result.add("</qmaproom>");
        
        return result;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        Iterable<String> lines = this.getXmlLinesFromQTileLayout();
        for (String line : lines)
        {
            sb.append(line + "\n");
        }
        return sb.toString();
    }
    
    //// Set Methods ////
    
    protected void setPropertiesFromXmlDocument(Document xmlDocument)
    {
        Element root = xmlDocument.getRootElement();
        Loading.ensureName(root, "qmaproom");
        
        // background color
        Function<String,Integer> backgroundFunc = input -> Color.create(Integer.decode(input), false);
        AttributeValue<Integer> attrBackground = new AttributeValue<>(root, "background", backgroundFunc); 
        
        // tileset
        Function<String,QTileset> tilesetFunc = QTileset::get;
        AttributeValue<QTileset> attrTileset = new AttributeValue<>(root, "tileset", tilesetFunc); 
        
        Loading.ensureAllValidAttributes(attrBackground, attrTileset);
        
        this.backgroundColor = attrBackground.getValue();
        this.qtileset = attrTileset.getValue();
        
        // layout
        Predicate<Element> layoutMatcher = input -> Loading.hasName(input, "layout");
        Consumer<Element> layoutAction = QMapRoom.this::setQTileLayoutFromXmlElement;
        Loading.applyActionToFirstMatchingXmlElementChild(root, layoutMatcher, layoutAction);
        
        // links
        Predicate<Element> linksMatcher = input -> Loading.hasName(input, "links");
        Consumer<Element> linksAction = QMapRoom.this::setMapLinksFromXmlElement;
        Loading.applyActionToFirstMatchingXmlElementChild(root, linksMatcher, linksAction);
    }
    
    protected void setQTileLayoutFromXmlElement(Element layoutElement)
    {
        int lineNum = -1;
        int y = -1;
        int x = -1;
        try
        {
            Loading.ensureName(layoutElement, "layout");
            
            // QTile layout
            String layoutValue = layoutElement.getValue();
            String[] layoutLines = layoutValue.split("(\n)+");
            
            int CODE_LEN = QTile.CODE_LENGTH;
            int maxLength = 0;
            List<QTile[]> qtileListOfArrays = new ArrayList<>();
            
            for (y = 0, lineNum = 0; lineNum < layoutLines.length; ++y, ++lineNum) 
            {
                String line = layoutLines[lineNum];
                
                //System.err.println("[" + line.length() + ":" + line + "]");
                int lineLength = (line.length() / CODE_LEN) * CODE_LEN;
                if (lineLength < CODE_LEN) { --y; continue; }
                
                maxLength = (lineLength > maxLength) ? lineLength : maxLength;
                
                QTile[] qtileArray = new QTile[lineLength / CODE_LEN];
                for (x = 0; x < qtileArray.length; ++x)
                {
                    int linePos = x * CODE_LEN;
                    String qtileCode = line.substring(linePos, linePos + CODE_LEN);
                    qtileCodes.add(qtileCode);
                    qtileArray[x] = this.qtileset.getNewQTile(qtileCode);
                    qtileArray[x].translate(x * QTileset.getTileWidth(), y * QTileset.getTileHeight());
                }
                qtileListOfArrays.add(qtileArray);
            }
            
            this.width = maxLength / CODE_LEN;
            this.height = qtileListOfArrays.size();
            this.qtileLayout = qtileListOfArrays.toArray(new QTile[0][]);
        }
        catch (RuntimeException rx)
        {
            throw rx;
        }
        catch (Exception ex)
        {
            System.err.printf("[qtilelayout: x = %3d, y = %3d]", x, y);
            System.err.println();
            ex.printStackTrace();
        }
    }
    
    protected void setMapLinksFromXmlElement(Element linksElement)
    {
        Loading.ensureName(linksElement, "links");
        
        this.mapLinks = new ArrayList<>();
        
        Consumer<Element> linkAction = QMapRoom.this::setMapLinkFromXmlElement;
        Loading.applyActionToXmlElementChildren(linksElement, linkAction);
    }
    
    protected void setMapLinkFromXmlElement(Element linkElement)
    {
        Loading.ensureName(linkElement, "link");
        
        Function<String,Integer> parseInteger = Integer::parseInt;
        
        AttributeValue<Integer> attrRow = new AttributeValue<>(linkElement, "row", parseInteger);
        AttributeValue<Integer> attrCol = new AttributeValue<>(linkElement, "col", parseInteger);
        AttributeValue<String> attrMap = new AttributeValue<>(linkElement, "map", Identity.STRING);
        AttributeValue<Integer> attrRow2 = new AttributeValue<>(linkElement, "row2", parseInteger);
        AttributeValue<Integer> attrCol2 = new AttributeValue<>(linkElement, "col2", parseInteger);
        
        Loading.ensureAllValidAttributes(attrRow, attrCol, attrMap, attrRow2, attrCol2);
        
        this.mapLinks.add(new MapLink(attrRow.getValue(), attrCol.getValue(), attrMap.getValue(), attrRow2.getValue(), attrCol2.getValue()));
    }
    
    protected void setImageFromLayout() 
    {
        DrawingImage _image = UserInterfaceProvider.get().createDrawingImage(getWidthPixels(), getHeightPixels(), DrawingImage.Config.ARGB_8888);
        try (DrawingContext ctx = _image.getContext())
        {
            for (int row = 0; row < this.qtileLayout.length; ++row)
            {
                for (int col = 0; col < this.qtileLayout[row].length; ++col)
                {
                    this.qtileLayout[row][col].drawToContext(ctx, col * QTileset.getTileWidth(), row * QTileset.getTileHeight());
                }
            }
            
            this.image = _image;
        }
        catch (Exception ex) 
        {
            System.err.println("DrawingContext error: " + ex);
        }
    }
    
    // used by QMapRoom editor program
    public void changeQTileAtPosition(String code, int row, int col)
    {
        if (row < 0 || this.qtileLayout.length <= row) { return; }
        if (col < 0 || this.qtileLayout[row].length <= col) { return; }
        
        this.qtileLayout[row][col] = this.qtileset.getNewQTile(code);
        this.setImageFromLayout();
    }
    
    @Override
    public void setLastCollidingTileWith(QCollidable c) 
    {
        QTile result = null;
        IntPoint2D posC = c.getIntPosition();
        int colC = posC.x / QTileset.getTileWidth();
        int rowC = posC.y / QTileset.getTileHeight();
        
        int rowFirst       = rowC - Q_SPACE; if (rowFirst < 0) { rowFirst = 0; }
        int rowLastPlusOne = rowC + Q_SPACE; if (rowLastPlusOne > this.height) { rowLastPlusOne = this.height; }
        
        int colFirst       = colC - Q_SPACE; if (colFirst < 0) { colFirst = 0; }
        int colLastPlusOne = colC + Q_SPACE; if (colLastPlusOne > this.width) { colLastPlusOne = this.width; }
        
        outer:
        for (int row = rowFirst; row < rowLastPlusOne; ++row)
        {
            QTile[] qtiles = this.qtileLayout[row];
            
            for (int col = colFirst; col < colLastPlusOne; ++col)
            {
                if (qtiles[col].collidesWith(c)) { result = qtiles[col]; break outer; }
            }
        }
        this.lastCollidingTile = result; 
    }
    
    public static int getWrappedValue(int val, int bound)
    {
        int result = val;
        if (result < 0) { result = bound + result; }
        return result;
    }
    
    public int getWrappedRowValue(int row) { return QMapRoom.getWrappedValue(row, this.height); }
    public int getWrappedColValue(int col) { return QMapRoom.getWrappedValue(col, this.width); }
    
    // I know this is really a get method, but it belongs next to the other collide method.
    public MapLink getCollidingMapLinkOrNull(QCollidable c)
    {
        MapLink result = null;
        
        for (MapLink link : this.mapLinks)
        {
            int linkRow = this.getWrappedRowValue(link.row);
            int linkCol = this.getWrappedColValue(link.col);
            try
            {
                QTile linkTile = this.qtileLayout[linkRow][linkCol];
                if (linkTile.collidesWith(c))
                {
                    result = link;
                    break;
                }
            }
            catch (RuntimeException rx)
            {
                System.err.println("[qmaproom getCollidingMapLinkOrNull]");
                System.err.println(link);
                System.err.printf("[linkRow = %s, linkCol = %s]\n", linkRow, linkCol);
                throw rx;
            }
        }
        
        return result;
    }
    
    
    //
    // Inner Static Class
    //
    
    public static class MapLink
    {
        public final int row;
        public final int col;
        public final String map;
        public final int row2;
        public final int col2;
        
        public MapLink(int _row, int _col, String _map, int _row2, int _col2)
        { this.row = _row; this.col = _col; this.map = _map; this.row2 = _row2; this.col2 = _col2; }
        
        @Override
        public String toString()
        {
            return String.format("<link row='%s' col='%s' map='%s' row2='%s' col2='%s' />", this.row, this.col, this.map, this.row2, this.col2);
        }
    }
}