/* 
 * Nicholas Saney 
 * 
 * Created: September 27, 2013
 * Modified: January 23, 2015
 * 
 * Loading.java
 * Loading class definition
 * 
 */

package chairosoft.quadrado;

import static chairosoft.quadrado.Functions.*;

import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.Rectangle;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

import nu.xom.*;

/** Helper functions for loading data from within JAR and XML files. */
public final /*static*/ class Loading
{
    //
    // Constructors
    //
    
    /**
     * Default constructor with {@code private} visibility.
     */
    private Loading() {}
    
    
	///////////////////
	//               //
	// Getting Files //
	//               //
	///////////////////
	
	/**
	 * Combines a folder path with a relative path.
	 * @param folderPath   The folder path.
	 * @param relativePath The relative path.
	 * @return The combination of the two paths.
	 */
	protected static String getPathInFolder(String folderPath, String relativePath)
	{ return String.format("%s/%s", folderPath, relativePath); }
	
	
	/**
	 * Gets an {@code InputStream} from an absolute path, looking for the 
	 * resource either within the current JAR or from the file system.
	 * @param absolutePath  The absolute path to the resource.
	 * @param usingInternal If true, look in the current JAR. Otherwise, 
	 *                      look for the resource on the file system.
	 * @return The {@code InputStream} corresponding to the given absolute path.
	 * @throws IOException If there is a problem finding the resource.
	 */
	protected static InputStream getInputStreamFromPath(String absolutePath, boolean usingInternal)
		throws IOException
	{
		return (usingInternal) 
            	? Loading.class.getResourceAsStream(Loading.getInternalPath(absolutePath))
            	: new FileInputStream(absolutePath);
	}
	
    /**
     * Returns the given path with "/" prepended. 
     * If the path already starts with "/", it is returned unchanged.
     * @param  path The path to use as a JAR-internal path.
     * @return A JAR-internal path (one starting with "/").
     */
    protected static String getInternalPath(String path)
    {
        return (path.charAt(0) == '/')
            ? path
            : ("/" + path);
    }
    
	
	///////////////
	//           //
	// Verbosity //
	//           //
	///////////////
	
	protected static PrintStream out = System.err;
	protected static volatile boolean verbose = false;
    public static void startVerbose() { verbose = true; }
    public static void stopVerbose() { verbose = false; }
	
	
	
    /**
	 * Returns a {@code DrawingImage} from the given absolute path. 
	 * @param absolutePath the name of the file to retrieve.
	 * @return the {@code DrawingImage} at the given path, or {@code null}
	 *         if there is any error attempting to load an image from the given path.
	 */
	public static DrawingImage getImage(String absolutePath)
	{
		return getImage(absolutePath, true);
	}
	
    public static DrawingImage getImage(String absolutePath, boolean usingInternal)
	{
        if (verbose)
        {
            Loading.out.printf("LOADER PATH (IMG): %s [internal=%s]\n", absolutePath, usingInternal);
        }
        
	    DrawingImage img = null;
	    try (InputStream input = Loading.getInputStreamFromPath(absolutePath, usingInternal))
	    {
	        img = DrawingImage.create(input);
	    }
	    catch (Exception ex)
	    {
            System.err.printf("Error loading file at %s. [internal=%s]\n", absolutePath, usingInternal); 
            throw new RuntimeException(ex);
        }
        return img;
	}
    
    
    public final static Function<String,Integer> TRANSPARENCY_DECODER_FUNCTION = new Function<String,Integer>() 
    { public Integer apply(String input) { return Integer.decode(input); } }; /* input -> Integer.decode(input) */ 
    
    /**
     * Applies transparency to an image where pixels are marked for transparency.
     * @param imgRaw           The image to apply transparency to.
     * @param transparentValue The value to make transparent.
     * @return The original image with pixels marked with the transparency value replaced with transparent.
     */
	public static DrawingImage applyTransparency(DrawingImage imgRaw, int transparentValue)
	{
        int rawWidth = imgRaw.getWidth();
        int rawHeight = imgRaw.getHeight();
        DrawingImage tileImage = DrawingImage.create(rawWidth, rawHeight, DrawingImage.Config.ARGB_8888);
        
        try (DrawingContext ctx = tileImage.getContext())
        {
            ctx.drawImage(imgRaw, 0, 0);
            for (int j = 0; j < rawHeight; ++j) 
            {
                for (int i = 0; i < rawWidth; ++i) 
                { Loading.applyTransparencyToPixel(tileImage, i, j, transparentValue); }
            }
		}
		catch (Exception ex) 
		{
			System.err.println("DrawingContext error: " + ex);
		}
        
        return tileImage;
	}
	
	protected static void applyTransparencyToPixel(DrawingImage bufImg, int x, int y, int transparentValue)
	{
	    if ((bufImg.getPixel(x, y) & 0x00FFFFFF) == transparentValue) 
	    {
            bufImg.setPixel(x, y, 0); 
        }
	}
	
	
	/**
     * Returns a parsed XML {@code Document} of the file at {@code absolutePath}.
     * @param absolutePath the name of the file to parse.
     * @return the parsed {@code Document}, or {@code null} if there is any
     *         error attempting to parse as XML the file at the given path.
     */
	public static Document getXmlDocument(String absolutePath)
	{
		return getXmlDocument(absolutePath, true);
	}
	
	public static Document getXmlDocument(String absolutePath, boolean usingInternal)
	{
        if (verbose)
        {
            Loading.out.printf("LOADER PATH (XML): %s [internal=%s]\n", absolutePath, usingInternal);
        }
        
	    Builder parser = new Builder();
	    Document doc = null;
	    InputStream input = null;
        try 
        {
            input = Loading.getInputStreamFromPath(absolutePath, usingInternal);
            doc = parser.build(input); 
        }
        catch (Exception ex) 
        {
            System.err.printf("Error parsing file at \"%s\". [internal=%s]\n", absolutePath, usingInternal);  
            throw new RuntimeException(ex);
        }
        finally 
        {
            try { if (input != null) { input.close(); } } 
            catch (Exception ex) { doc = null; } 
        }
        return doc;
	}
	
	
	
	/////////////////////////////////
	//                             //
	// Working with XML attributes //
	//                             //
	/////////////////////////////////
	
	/**
	 * Checks to see if all of the given attributes refer to non-{@code null} values.
	 * @param attributes the attributes to check
	 * @return true if all of the given attribute refer to non-{@code null} values;
	 *         false otherwise.
	 */
	public static boolean areAllValidAttributes(AttributeValue... attributes)
	{
	    return (null == Loading.getFirstInvalidAttribute(attributes));
	}
	
	public static AttributeValue getFirstInvalidAttribute(AttributeValue... attributes)
	{
		AttributeValue result = null;
		for (AttributeValue attribute : attributes) 
	    { if (!attribute.isValid()) { result = attribute; break; } }
		return result;
	}
	
	public static void ensureAllValidAttributes(AttributeValue... attributes)
	{
		AttributeValue firstInvalidAttribute = getFirstInvalidAttribute(attributes);
		if (null != firstInvalidAttribute)
		{
        	throw new XmlParsingException("Invalid attribute: " + firstInvalidAttribute);
		}
	}
	
	
	public static class AttributeValue<T>
	{
	    protected Element             parent;
	    protected String              name;
	    protected Function<String, T> parser;
	    protected Attribute           attribute;
	    
	    public AttributeValue(Element _parent, String _name, Function<String,T> _parser)
	    {
	        this.parent     = _parent;
	        this.name       = _name;
	        this.parser     = _parser;
	        this.attribute  = _parent.getAttribute(_name);
	    }
	    
	    public boolean isValid() { return (null != this.attribute); }
	    public T getValue() { return this.parser.apply(this.attribute.getValue()); }
	    
        @Override
	    public String toString()
	    {
	    	String value = this.isValid() ? this.getValue().toString() : "(invalid)";
	    	return String.format("[Attribute %s in element %s, value = %s]", this.name, this.parent, value);
	    }
	}
	
	
	public static class RangedIntAttributeParser implements Function<String,Integer>
	{
		String valueName;
		int    inclusiveMin;
		int    exclusiveMax;
		
		public RangedIntAttributeParser(String _valueName, int _inclusiveMin, int _exclusiveMax)
		{
			this.valueName    = _valueName;
			this.inclusiveMin = _inclusiveMin;
			this.exclusiveMax = _exclusiveMax;
		}
		
        @Override
		public Integer apply(String input)
		{
			return Loading.parseRangedInt(input, this.valueName, this.inclusiveMin, this.exclusiveMax);
		}
	}
	
	public static int parseRangedInt(String input, String valueName, int inclusiveMin, int exclusiveMax)
	{
		int result = Integer.parseInt(input);
		if ((result < inclusiveMin) || (exclusiveMax < result)) 
		{
			String exceptionMessage = String.format("%s out of range [%s,%s): %s", valueName, inclusiveMin, exclusiveMax, result);
			throw new RuntimeException(exceptionMessage); 
		}
		return result;
	}
	
	
	
	///////////////////////////////
	//                           //
	// Working with XML Elements //
	//                           //
	///////////////////////////////
	
	public static class XmlParsingException extends RuntimeException
	{
		public XmlParsingException(String message) { super(message); }
		
		public XmlParsingException(Element xmlElement, String correctName)
		{
			super(String.format("Unable to parse element with name '%s'. Expected '%s'", xmlElement.getLocalName(), correctName));
		}
	}
	
	
	/**
	 * Tests an XML Element to see if it has a particular name.
	 * @param xmlElement The XML element to test.
	 * @param name       The name to test against.
	 * @return {@code true} if the given {@code Element} has the given name.
	 */
	public static boolean hasName(Element xmlElement, String name)
	{ return (xmlElement.getLocalName() == name); }
	
	
	/**
	 * Ensures that the given XML Element has the given name, and throws a 
	 * runtime exception if it does not.
	 * @param xmlElement The XML element to test.
	 * @param name       The name to test against.
	 */
	public static void ensureName(Element xmlElement, String name)
	{
		if (!hasName(xmlElement, name))
		{
        	throw new XmlParsingException(xmlElement, name);
		}
	}
    
    public static class PredicateMatchingElementByAttribute implements Predicate<Element>
    {
        public final String attributeName;
        public final String attributeValue;
        public PredicateMatchingElementByAttribute(String _attributeName, String _attributeValue)
        {
            this.attributeName = _attributeName;
            this.attributeValue = _attributeValue == null ? "" : _attributeValue;
        }
        
        @Override
        public boolean test(Element e) { return this.attributeValue.equals(e.getAttribute(this.attributeName).getValue()); }
    }
	
	
	/**
	 * Applies the given {@code Consumer} to each of the given {@code Element}'s
	 * child nodes. 
	 * @param xmlElement the parent node 
	 * @param action     the action to apply to the parent node's children
	 */
	public static 
	void applyActionToXmlElementChildren(Element xmlElement, Consumer<Element> action)
	{
        int childCount = xmlElement.getChildCount();
        for (int i = 0; i < childCount; ++i)
        {
            Node child = xmlElement.getChild(i);
            if (child instanceof Element) 
            { action.accept((Element)child); }
        }
	}
	
	
	/**
	 * Applies the given {@code Consumer} to the first of the given {@code Element}'s
	 * child nodes that matches as defined by the given {@code Predicate}. 
	 * @param xmlElement the parent node 
	 * @param matcher    predicate to test child nodes with
	 * @param action     the action to apply to the first matching child
	 */
	public static 
	void applyActionToFirstMatchingXmlElementChild(Element xmlElement, Predicate<Element> matcher, Consumer<Element> action)
	{
        int childCount = xmlElement.getChildCount();
        for (int i = 0; i < childCount; ++i)
        {
            Node child = xmlElement.getChild(i);
            if (child instanceof Element && matcher.test((Element)child)) 
            { action.accept((Element)child); break; }
        }
	}
	
	
	/**
	 * Applies the given {@code Function} to the first of the given {@code Element}'s
	 * child nodes that matches as defined by the given {@code Predicate}. 
     * @param <T>        the return type of the given function
	 * @param xmlElement the parent node 
     * @param matcher    predicate to test child nodes with
	 * @param function   the function to apply to the first matching child
     * @return the result of the function if applied, or null if not.
	 */
	public static <T>
	T applyFunctionToFirstMatchingXmlElementChild(Element xmlElement, Predicate<Element> matcher, Function<Element,T> function)
	{
        T result = null;
        int childCount = xmlElement.getChildCount();
        for (int i = 0; i < childCount; ++i)
        {
            Node child = xmlElement.getChild(i);
            if (child instanceof Element && matcher.test((Element)child)) 
            { result = function.apply((Element)child); break; }
        }
        return result;
	}
	
	
	
	///////////////////////////////////////
	//                                   //
	// Parsing objects from XML Elements //
	//                                   //
	///////////////////////////////////////
	
	/**
	 * Gets a {@code FloatPoint2D} object from XML of the form 
	 * {@code <point x="int" y="int"/>}.
     * @param xmlPoint      the XML element to parse as a point
     * @param xInclusiveMin the minimum value to accept for x
     * @param yInclusiveMin the minimum value to accept for y
     * @param xExclusiveMax the maximum value to accept for x
     * @param yExclusiveMax the maximum value to accept for y
     * @return a {@code FloatPoint2D} object with the values specified by the XML.
	 */
	public static FloatPoint2D parseFloatPoint2D(Element xmlPoint, int xInclusiveMin, int yInclusiveMin, int xExclusiveMax, int yExclusiveMax)
	{
		Loading.ensureName(xmlPoint, "point");
		
		AttributeValue<Integer> attrX = new AttributeValue<>(xmlPoint, "x", new RangedIntAttributeParser("Point x-value", xInclusiveMin, xExclusiveMax));
		AttributeValue<Integer> attrY = new AttributeValue<>(xmlPoint, "y", new RangedIntAttributeParser("Point y-value", yInclusiveMin, yExclusiveMax));
		Loading.ensureAllValidAttributes(attrX, attrY);
		
		return new FloatPoint2D((float)attrX.getValue(), (float)attrY.getValue());
	}
	
	/**
	 * Gets a {@code Rectangle} object from XML of the form 
	 * {@code <rectangle x0="int" y0="int" width="int" height="int"/>}.
     * @param xmlRectangle the XML element to parse as a rectangle
     * @param minWidth     the minimum width to accept for the rectangle
     * @param minHeight    the minimum height to accept for the rectangle
     * @return a {@code Rectangle} object with the values specified by the XML.
	 */
	public static Rectangle parseRectangle(Element xmlRectangle, int minWidth, int minHeight)
	{
		Loading.ensureName(xmlRectangle, "rectangle");
		
		Function<String,Integer> parseInteger = new Function<String,Integer>() { public Integer apply(String input) { return Integer.parseInt(input); } };
		/* input -> Integer.parseInt(input) */
		
		AttributeValue<Integer> attrX0 = new AttributeValue<>(xmlRectangle, "x0", parseInteger);
		AttributeValue<Integer> attrY0 = new AttributeValue<>(xmlRectangle, "y0", parseInteger);
		AttributeValue<Integer> attrWidth = new AttributeValue<>(xmlRectangle, "width", new RangedIntAttributeParser("Rectangle width", minWidth, Integer.MAX_VALUE));
		AttributeValue<Integer> attrHeight = new AttributeValue<>(xmlRectangle, "height", new RangedIntAttributeParser("Rectangle height", minHeight, Integer.MAX_VALUE));
		Loading.ensureAllValidAttributes(attrX0, attrY0, attrWidth, attrHeight);
		
		return new Rectangle(attrX0.getValue(), attrY0.getValue(), attrWidth.getValue(), attrHeight.getValue());
	}
	
	
	
}