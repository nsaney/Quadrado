/* 
 * Nicholas Saney 
 * 
 * Created: December 22, 2014
 * Modified: January 22, 2015
 * 
 * QTileset.java
 * QTileset class definition
 * 
 */

package chairosoft.quadrado;

import static chairosoft.quadrado.Functions.*;
import static chairosoft.quadrado.Loading.*;

import chairosoft.ui.geom.Point2D;
import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.IntPoint2D;
import chairosoft.ui.geom.Rectangle;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import java.io.*; 
import java.util.*;

import nu.xom.*;

/**
 * A set of {@code QTile} objects to be used by a {@code QMapRoom}.
 */
public class QTileset
{
    // 
    // Constants
    // 
    
    public static final String SPEC_FOLDER         = "img";
    public static final String QTILESETS_FILENAME  = "qtilesets.xml";
    public static final String QTILESETS_FULL_PATH = Loading.getPathInFolder(SPEC_FOLDER, QTILESETS_FILENAME);
    
    
    // 
    // Static Variables
    // 
    
    protected static String                imageFolder;
    protected static int                   tileWidth;
    protected static int                   tileHeight;
    protected static Map<String, QTileset> tilesetMap;
    
    
    // 
    // Static Initialization
    //
    
    static
    {
		try
		{
			Document doc = Loading.getXmlDocument(QTILESETS_FULL_PATH);
            QTileset.initializeFromXmlDocument(doc);
		}
		catch (Exception ex)
		{
			System.err.println("[qtilesets file]");
			ex.printStackTrace();
        }
    }
    
    /**
     * Helper for static initialization.
     * @param xmlDocument the XML document from which to initialize the QTileset class.
     */
    private final static void initializeFromXmlDocument(Document xmlDocument)
    {
        Element root = xmlDocument.getRootElement();
        Loading.ensureName(root, "qtilesets");
        
        AttributeValue<String> attrFolder = new AttributeValue<>(root, "folder", new Identity<String>());
        AttributeValue<Integer> attrTileWidth = new AttributeValue<>(root, "tileWidth", new RangedIntAttributeParser("QTileset tileWidth", 1, Integer.MAX_VALUE));
        AttributeValue<Integer> attrTileHeight = new AttributeValue<>(root, "tileHeight", new RangedIntAttributeParser("QTileset tileHeight", 1, Integer.MAX_VALUE));
        
        Loading.ensureAllValidAttributes(attrFolder, attrTileWidth, attrTileHeight);
        
        QTileset.imageFolder = Loading.getPathInFolder(QTileset.SPEC_FOLDER, attrFolder.getValue());
        QTileset.tileWidth = attrTileWidth.getValue();
        QTileset.tileHeight = attrTileHeight.getValue();
        QTileset.tilesetMap = new HashMap<>(); // this is filled in as needed by QTileset.load()
    }
    
    
    // 
    // Instance Variables
    // 
    
    protected String             code;
    protected int                tilesheetTransparentValue;
    protected String             tilesheetLocation;
    protected Map<String, QTile> tilesMap;
    
    
    // 
    // Constructors
    // 
    
    /**
     * Constructs a QTileset with all of its properties fully specified.
     * @param _code                      the QTileset's code
     * @param _tilesheetTransparentValue the QTileset's transparent value
     * @param _tilesheetLocation         the QTileset's tilesheet location
     * @param _tilesMap                  the QTileset's tile {@code Map}
     */
    protected QTileset
    (
        String _code, 
        int _tilesheetTransparentValue, 
        String _tilesheetLocation,
        Map<String, QTile> _tilesMap
    )
    {
        this.code = _code;
        this.tilesheetTransparentValue = _tilesheetTransparentValue;
        this.tilesheetLocation = _tilesheetLocation;
        this.tilesMap = _tilesMap;
    }
    
    
    // 
    // Instance Methods
    // 
    
    /**
     * Gets a new QTile based on the master copy in this QTileset.
     * @param code the code of the QTile to get a copy of
     * @return a new copy of the specified QTile.
     */
    public QTile getNewQTile(String code)
    {
        QTile result = null;
        QTile qt = this.tilesMap.get(code);
        if (qt != null) { result = new QTile(qt); }
        return result;
    }
    
    /**
     * Disposes this QTileset and each QTile in it.
     */
    public void dispose()
    {
        Set<String> codes = this.getQTileCodes();
        for (String code : codes)
        {
            QTile qt = this.tilesMap.remove(code);
            if (qt != null) { qt.dispose(); }
        }
    }
    
    /**
     * Gets the set of QTile codes in this QTileset.
     * @return the set of QTile codes in this QTileset's tile {@code Map}.
     */
    public Set<String> getQTileCodes() { return this.tilesMap.keySet(); }
    
    
    // 
    // Static Methods
    // 
    
    /**
     * Gets the width for tiles in this Quadrado application. 
     * @return The width for tiles in this Quadrado application. 
     */
    public static int getTileWidth() { return QTileset.tileWidth; }
    
    /** 
     * Gets the height for tiles in this Quadrado application. 
     * @return The height for tiles in this Quadrado application. 
     */
    public static int getTileHeight() { return QTileset.tileHeight; }
    
    /** 
     * Gets the QTileset associated with the given code. If there is not currently an 
     * associated tileset, then it will be loaded from XML. If it is not specified in 
     * the XML, a runtime exception will be thrown.
     * @param code the code of the QTileset to get or to load and get
     * @return The QTileset associated with the given code.
     */
    public static QTileset get(String code)
    {
        if (!QTileset.tilesetMap.containsKey(code))
        {
            QTileset.tilesetMap.put(code, QTileset.load(code));
        }
        return QTileset.tilesetMap.get(code);
    }
    
    /**
     * Removes and disposes the QTileset associated with the given code. Does nothing 
     * if there is not currently an associated tileset.
     * @param code the code of the QTileset to remove and dispose
     */
    public static void disposeAndRemove(String code)
    {
        if (QTileset.tilesetMap.containsKey(code))
        {
            QTileset tileset = QTileset.tilesetMap.remove(code);
            if (tileset != null) { tileset.dispose(); }
        }
    }
    
    /**
     * Loads the QTileset associated with the given code.
     * @param code the code of the QTileset to load
     * @return The QTileset specified by the given code.
     */
    protected static QTileset load(final String code)
    {
        QTileset result = null;
		try
		{
			Document doc = Loading.getXmlDocument(QTILESETS_FULL_PATH);
            Element root = doc.getRootElement();
            Loading.ensureName(root, "qtilesets");
            
			result = Loading.applyFunctionToFirstMatchingXmlElementChild
			(
			    root,
                new Loading.PredicateMatchingElementByAttribute("code", code),
			    new Function<Element, QTileset>() { public QTileset apply(Element child) { return getQTilesetFromXmlElement(child); } }
			    // child -> getQTilesetFromXmlElement(child)
			);
		}
		catch (Exception ex)
		{
			System.err.println("[qtilesets file]");
			ex.printStackTrace();
        }
        return result;
    }
    
    /**
     * Gets a {@code QTileset} from its XML Element description.
     * @param xmlQTileset the XML element to parse as a QTileset
     * @return The {@code QTileset} resulting from parsing the given XML element.
     */
    protected static QTileset getQTilesetFromXmlElement(Element xmlQTileset)
    {
        QTileset result = null;
        
        try
		{
            Loading.ensureName(xmlQTileset, "qtileset");
            
            AttributeValue<String> attrCode = new AttributeValue<String>(xmlQTileset, "code", new Identity<String>());
            AttributeValue<Integer> attrTransparent = new AttributeValue<>(xmlQTileset, "transparent", Loading.TRANSPARENCY_DECODER_FUNCTION); 
            AttributeValue<String> attrImage = new AttributeValue<String>(xmlQTileset, "image", new Identity<String>());
            Loading.ensureAllValidAttributes(attrCode, attrTransparent, attrImage);
			
            String code = attrCode.getValue();
            int transparent = attrTransparent.getValue();
            String location = attrImage.getValue();
            String tilesetPath = Loading.getPathInFolder(QTileset.imageFolder, location);
            final List<DrawingImage> tileImages = QTileset.getTilesetImages(tilesetPath, transparent);
            final Map<String, QTile> tiles = new HashMap<>();
            
			Loading.applyActionToXmlElementChildren
			(
			    xmlQTileset,
			    new Consumer<Element>() { public void accept(Element child) { QTile qt = QTile.getQTileFromXmlElement(child, tileImages); tiles.put(qt.code, qt); } }
			    // child -> { QTile qt = QTile.getQTileFromXmlElement(child, tileImages); tiles.put(qt.code, qt); }
			);
            
            result = new QTileset(code, transparent, location, tiles);
		}
		catch (Exception ex)
		{
			System.err.println("[qtileset]");
			ex.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Gets a tilesheet image and breaks it up into the set of tiles it holds.
     * @param tilesetPath               the path to the tileset image file
     * @param tilesheetTransparentValue the pixel value to treat as transparent
     * @return a {@code List} of the tile {@code Image}s in the tileset at the given path.
     */
    protected static List<DrawingImage> getTilesetImages(String tilesetPath, int tilesheetTransparentValue)
    {
        ArrayList<DrawingImage> result = new ArrayList<>();
        DrawingImage tilesetImageRaw = Loading.getImage(tilesetPath);
        DrawingImage tilesetImage = Loading.applyTransparency(tilesetImageRaw, tilesheetTransparentValue);
        
        for (int y = 0; y < tilesetImage.getHeight(); y += QTileset.getTileHeight())
        {
            for (int x = 0; x < tilesetImage.getWidth(); x += QTileset.getTileWidth())
            {
                DrawingImage tileImage = tilesetImage.getImmutableSubimage(x, y, QTileset.getTileWidth(), QTileset.getTileHeight());
                result.add(tileImage);
            }
        }
        
        return result;
    }
}