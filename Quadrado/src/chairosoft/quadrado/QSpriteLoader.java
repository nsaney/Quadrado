/* 
 * Nicholas Saney 
 * 
 * Created: November 14, 2013
 * 
 * QSpriteLoader.java
 * QSpriteLoader class definition
 * (previously included within QSprite)
 * 
 */

package chairosoft.quadrado;


import static chairosoft.quadrado.QSprite.*;

import chairosoft.ui.geom.Point2D;
import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.IntPoint2D;
import chairosoft.ui.geom.Rectangle;

import chairosoft.ui.graphics.Color;
import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import chairosoft.util.function.*;
import chairosoft.util.Loading;
import static chairosoft.util.Loading.*;

import java.io.*; 
import java.util.*;


import nu.xom.*;


public class QSpriteLoader
{
    //
    // Static Variables
    // 
    
    protected static String imageFolder; // defined later
    
    
    //
    // Static Methods
    //
    
    protected static Map<String,QSprite> getQSpriteMapFromXmlDocument(Document xmlDocument)
    {
        final Map<String,QSprite> result = new HashMap<>();
        try
        {
            Element root = xmlDocument.getRootElement();
            Loading.ensureName(root, "qsprites");
            
            AttributeValue<String> attrFolder = new AttributeValue<>(root, "folder", new Identity<String>());
            Loading.ensureAllValidAttributes(attrFolder);
            
            imageFolder = QSprite.specFolder + "/" + attrFolder.getValue();
            
            Loading.applyActionToXmlElementChildren
            (
                root,
                new Consumer<Element>() { public void accept(Element child) { 
                    QSprite qs = getQSpriteFromXmlElement(child, result); 
                    if (qs != null) { result.put(qs.code, qs); } 
                } }
                //  child -> { 
                //      QSprite qs = getQSpriteFromXmlElement(child); 
                //      if (qs != null) { result.put(qs.code, qs); } 
                //  } 
            );
        }
        catch (Exception ex)
        {
            System.err.print("[qsprites]");
            ex.printStackTrace();
        }
        return result;
    }
    
    protected static QSprite getQSpriteFromXmlElement
    (
        Element xmlQSprite, 
        Map<String, QSprite> currentSpriteMap
    )
    {
        QSprite result = null;
        try
        {
            Loading.ensureName(xmlQSprite, "qsprite");
            
            AttributeValue<String> attrCode = new AttributeValue<String>(xmlQSprite, "code", new Identity<String>());
            AttributeValue<Integer> attrWidth = new AttributeValue<Integer>(xmlQSprite, "width", new RangedIntAttributeParser("QTile width", 1, Integer.MAX_VALUE));
            AttributeValue<Integer> attrHeight = new AttributeValue<Integer>(xmlQSprite, "height", new RangedIntAttributeParser("QTile height", 1, Integer.MAX_VALUE));
            Loading.ensureAllValidAttributes(attrCode, attrWidth, attrHeight);
            
            String codeValue = attrCode.getValue();
            if (currentSpriteMap.containsKey(codeValue)) { return result; }
            
            int spriteWidth = attrWidth.getValue();
            int spriteHeight = attrHeight.getValue();
            
            
            boolean parsedSpriteSheet = false;
            boolean parsedDefaultState = false;
            List<DrawingImage>       spriteImages       = new ArrayList<>();
            Map<String, OffsetShape> spriteShapeMap     = new HashMap<>();
            Map<String, Animation>   spriteAnimationMap = new HashMap<>();
            Map<String, State>       spriteStateMap     = new HashMap<>();
            String spriteDefaultStateCode = "";
            
            // using for loop instead of Loading.applyActionToXmlElementChildren(...)
            // because we would need to pass all of the above variables by reference
            int childCount = xmlQSprite.getChildCount();
            for (int i = 0; i < childCount; ++i)
            {
                Node child = xmlQSprite.getChild(i);
                if (child instanceof Element)
                {
                    Element childElement = (Element)child;
                    switch (childElement.getLocalName())
                    {
                        case "spritesheet": 
                            if (parsedSpriteSheet) { continue; }
                            spriteImages = parseSpriteSheetFromXmlElement(childElement, spriteWidth, spriteHeight);
                            parsedSpriteSheet = true;
                            break;
                        case "shapes": addShapesToMapFromXmlElement(childElement, spriteShapeMap); break;
                        case "animations": 
                            if (parsedSpriteSheet) { addAnimationsToMapFromXmlElement(childElement, spriteAnimationMap, spriteImages); }
                            break;
                        case "states": 
                            if (!parsedDefaultState) 
                            {
                                spriteDefaultStateCode = childElement.getAttribute("default").getValue();
                                parsedDefaultState = (spriteDefaultStateCode != null);
                            }
                            addStatesToMapFromXmlElement(childElement, spriteStateMap); 
                            break;
                        default: break;
                    }
                }
            }
            
            result = new QSprite(
                codeValue, 
                spriteImages, 
                spriteShapeMap, 
                spriteAnimationMap, 
                spriteStateMap, 
                spriteDefaultStateCode
            );
        }
        catch (Exception ex)
        {
            System.err.print("[qsprite]");
            ex.printStackTrace();
        }
        return result;
    }
    
    protected static List<DrawingImage> parseSpriteSheetFromXmlElement
    (
        Element xmlSpriteSheet, 
        int spriteWidth, 
        int spriteHeight
    ) 
    {
        ArrayList<DrawingImage> result = null;
        try
        {
            Loading.ensureName(xmlSpriteSheet, "spritesheet");
            
            AttributeValue<String> attrImage = new AttributeValue<String>(xmlSpriteSheet, "image", new Identity<String>());
            AttributeValue<Integer> attrTransparent = new AttributeValue<>(xmlSpriteSheet, "transparent", Loading.TRANSPARENCY_DECODER_FUNCTION); 
            Loading.ensureAllValidAttributes(attrImage, attrTransparent);
            
            String imageValue = Loading.getPathInFolder(imageFolder, attrImage.getValue());
            DrawingImage spriteSheetImageRaw = Loading.getImage(imageValue);
            int transparentValue = attrTransparent.getValue();
            
            DrawingImage spriteSheetImage = Loading.applyTransparency(spriteSheetImageRaw, transparentValue);
            result = new ArrayList<DrawingImage>();
            
            for (int y = 0; y < spriteSheetImage.getHeight(); y += spriteHeight)
            {
                for (int x = 0; x < spriteSheetImage.getWidth(); x += spriteWidth)
                {
                    DrawingImage spriteImage = spriteSheetImage.getImmutableSubimage(x, y, spriteWidth, spriteHeight);
                    result.add(spriteImage);
                }
            }
        }
        catch (Exception ex)
        {
            System.err.print("[spritesheet]");
            ex.printStackTrace();
            result = null;
        }
        
        return result;
    }
    
    protected static void addShapesToMapFromXmlElement(Element xmlShapes, final Map<String, OffsetShape> spriteShapeMap) 
    {
        try
        {
            Loading.ensureName(xmlShapes, "shapes");
            
            Loading.applyActionToXmlElementChildren
            (
                xmlShapes,
                new Consumer<Element>() { public void accept(Element child) { addShapeToMapFromElement(child, spriteShapeMap); } }
                // child -> addShapeToMapFromElement(child, spriteShapeMap)
            );
        }
        catch (Exception ex)
        {
            System.err.print("[shapes]");
            ex.printStackTrace();
        }
    }
    
    protected static void addShapeToMapFromElement(Element xmlShape, Map<String, OffsetShape> spriteShapeMap)
    {
        switch (xmlShape.getLocalName())
        {
            case "rectangle": addOffsetRectangleToMapFromElement(xmlShape, spriteShapeMap); break;
            default: break;
        }
    }
    
    protected static void addOffsetRectangleToMapFromElement(Element xmlRectangle, Map<String, OffsetShape> spriteShapeMap)
    {
        try
        {
            Loading.ensureName(xmlRectangle, "rectangle");
            
            AttributeValue<String> attrCode = new AttributeValue<>(xmlRectangle, "code", new Identity<String>());
            Rectangle r = Loading.parseRectangle(xmlRectangle, 1, 1);
            Loading.ensureAllValidAttributes(attrCode);
            
            String codeValue = attrCode.getValue();
            if (spriteShapeMap.containsKey(codeValue)) { return; }
            
            QCollidable rectangle = new QCollidable(0, 0, r.width, r.height);
            spriteShapeMap.put(codeValue, new OffsetShape(codeValue, r.x, r.y, rectangle));
        }
        catch (Exception ex)
        {
            System.err.print("[offsetshape:rectangle]");
            ex.printStackTrace();
        }
    }
    
    protected static void addAnimationsToMapFromXmlElement
    (
        Element xmlAnimations, 
        final Map<String, Animation> spriteAnimationMap, 
        final List<DrawingImage> spriteImages
    ) 
    {
        try
        {
            Loading.ensureName(xmlAnimations, "animations");
            Loading.applyActionToXmlElementChildren(
                xmlAnimations,
                new Consumer<Element>() { public void accept(Element child) { addAnimationToMapFromXmlElement(child, spriteAnimationMap, spriteImages); } }
                /* child -> addAnimationToMapFromXmlElement(child, spriteAnimationMap, spriteImages) */
            );
        }
        catch (Exception ex)
        {
            System.err.print("[animations]");
            ex.printStackTrace();
        }
    }
    
    protected static void addAnimationToMapFromXmlElement
    (
        Element xmlAnimation, 
        Map<String, Animation> spriteAnimationMap, 
        final List<DrawingImage> spriteImages
    )
    {
        try
        {
            Loading.ensureName(xmlAnimation, "animation");
            
            AttributeValue<String> attrCode = new AttributeValue<>(xmlAnimation, "code", new Identity<String>());
            AttributeValue<String> attrRepeat = new AttributeValue<>(xmlAnimation, "repeat", new Identity<String>());
            Loading.ensureAllValidAttributes(attrCode);
            
            String codeValue = attrCode.getValue();
            if (spriteAnimationMap.containsKey(codeValue)) { return; }
            
            boolean repeatValue = true;
            if (attrRepeat.isValid())
            {
                String repeatString = attrRepeat.getValue();
                if (repeatString.equalsIgnoreCase("false") || repeatString.equalsIgnoreCase("no") || repeatString.equalsIgnoreCase("0"))
                {
                    repeatValue = false;
                }
            }
            
            final ArrayList<Animation.Frame> frames = new ArrayList<Animation.Frame>();
            Loading.applyActionToXmlElementChildren(
                xmlAnimation,
                new Consumer<Element>() { public void accept(Element child) { addFrameToListFromXmlElement(child, frames, spriteImages); } }
                /* child -> addAnimationToMapFromXmlElement(child, spriteAnimationMap, spriteImages) */
            );
            
            spriteAnimationMap.put(codeValue, new Animation(codeValue, repeatValue, frames));
        }
        catch (Exception ex)
        {
            System.err.print("[animation]");
            ex.printStackTrace();
        }    
    }
    
    protected static void addFrameToListFromXmlElement
    (
        Element xmlFrame, 
        List<Animation.Frame> frames, 
        final List<DrawingImage> spriteImages
    )
    {
        try
        {
            Loading.ensureName(xmlFrame, "frame");
            
            AttributeValue<Integer> attrImage = new AttributeValue<>(xmlFrame, "image", new RangedIntAttributeParser("Frame image index", 0, spriteImages.size()));
            AttributeValue<Integer> attrDuration = new AttributeValue<>(xmlFrame, "duration", new RangedIntAttributeParser("Frame duration", 0, Integer.MAX_VALUE));
            Loading.ensureAllValidAttributes(attrImage, attrDuration);
            
            int imageIndex = attrImage.getValue();
            DrawingImage frameImage = spriteImages.get(imageIndex);
            
            int durationValue = attrDuration.getValue();
            
            frames.add(new Animation.Frame(frameImage, durationValue));
        }
        catch (Exception ex)
        {
            System.err.print("[frame]");
            ex.printStackTrace();
        }
    }
    
    protected static void addStatesToMapFromXmlElement(Element xmlStates, final Map<String, State> spriteStateMap)
    {
        try
        {
            Loading.ensureName(xmlStates, "states");
            Loading.applyActionToXmlElementChildren(
                xmlStates,
                new Consumer<Element>() { public void accept(Element child) { addStateToMapFromXmlElement(child, spriteStateMap); } }
                /* child -> addStateToMapFromXmlElement(child, spriteStateMap) */
            );
        }
        catch (Exception ex)
        {
            System.err.print("[states]");
            ex.printStackTrace();
        }
    }
    
    protected static void addStateToMapFromXmlElement(Element xmlState, Map<String, State> spriteStateMap)
    {
        try
        {
            Loading.ensureName(xmlState, "state");
            
            AttributeValue<String> attrCode = new AttributeValue<>(xmlState, "code", new Identity<String>());
            AttributeValue<String> attrShape = new AttributeValue<>(xmlState, "shape", new Identity<String>());
            AttributeValue<String> attrAnimation = new AttributeValue<>(xmlState, "animation", new Identity<String>());
            Loading.ensureAllValidAttributes(attrCode, attrShape, attrAnimation);
            
            String codeValue = attrCode.getValue();
            if (spriteStateMap.containsKey(codeValue)) { return; }
            
            String shapeValue = attrShape.getValue();
            String animationValue = attrAnimation.getValue(); 
            
            spriteStateMap.put(codeValue, new State(codeValue, shapeValue, animationValue)); 
        }
        catch (Exception ex)
        {
            System.err.print("[state]");
            ex.printStackTrace();
        }
    }
}