/* 
 * Nicholas Saney 
 * 
 * Created: February 11, 2013
 * 
 * QSprite.java
 * QSprite class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.IntPoint2D;

import chairosoft.ui.graphics.Color;
import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import chairosoft.util.function.*;
import chairosoft.util.Loading;
import static chairosoft.util.Loading.*;

import java.io.*; 
import java.util.*;

import nu.xom.*;

/**
 * A sprite specified in the /img/qsprites.xml configuration file.
 */
public class QSprite extends QPhysical2D
{
	
	//
	// Static Constants
	//
	
	public static final float MIN_MOVE_DISTANCE = 0.01f;
	
	
	//
	// Static Variables
	// 
	
	protected static Map<String, QSprite> spriteMap = null;
	protected static String               specFolder = "img";
    protected static String               qspritesFilename = "qsprites.xml";
	protected static String               qSpritesPath = Loading.getPathInFolder(specFolder, qspritesFilename);
	
	
	//
	// Instance Constants
	//
	
	public final String code;
	
	
	//
	// Instance Variables
	//
	
	protected List<DrawingImage>       images       = new ArrayList<>();
	protected Map<String, OffsetShape> shapeMap     = new HashMap<>();
	protected Map<String, Animation>   animationMap = new HashMap<>();
	protected Map<String, State>       stateMap     = new HashMap<>();
	
	protected String currentStateCode;
	
	protected State       currentState;
	protected Animation   currentAnimation;
	protected OffsetShape currentOffsetShape;
	protected IntPoint2D  currentOffset;
	
	
	//
	// Constructors
	//
	
	public QSprite
	(
		String                   _code, 
		List<DrawingImage>       _images, 
		Map<String, OffsetShape> _shapeMap, 
		Map<String, Animation>   _animationMap, 
		Map<String, State>       _stateMap, 
		String                   _defaultStateCode
	)
	{
		this.code          = _code;
		this.images        = _images;
		this.shapeMap      = _shapeMap;
		this.animationMap  = _animationMap;
		this.stateMap      = _stateMap;
		this.setCurrentStateCode(_defaultStateCode);
	}
	
	public QSprite(QSprite qs)
	{
		this(
			qs.code, 
			qs.images, 
			qs.shapeMap, 
			qs.animationMap, 
			qs.stateMap, 
			qs.currentStateCode
		);
	}
	
	public QSprite(String spriteCode) { this(spriteMap.get(spriteCode)); }
	
	
	//
	// Static Initializer
	//
	// !! TODO !! --> don't load all when class loads. load as requested. allow disposal.
	//
	
	static 
	{
		try
		{
			Document doc = Loading.getXmlDocument(QSprite.qSpritesPath);
			QSprite.spriteMap = QSpriteLoader.getQSpriteMapFromXmlDocument(doc);
		}
		catch (Exception ex)
		{
			System.err.println("[qsprites file]");
			ex.printStackTrace();
		}
	}
	
	
	//
	// Static Methods
	//
	
	public static float keepWithinRange(float num, float bound)
	{
		if (bound < 0) { throw new RuntimeException("[keepWithinRange] Parameter 'bound' must be positive. "); }
		float sign = Math.signum(num);
		return (Math.abs(num) > bound) ? (sign * bound) : num;
	}
	
	public static QSprite getNewQSprite(String code)
	{
		QSprite qs = QSprite.spriteMap.get(code);
		QSprite result = null;
		if (qs != null) { result = new QSprite(qs.code, qs.images, qs.shapeMap, qs.animationMap, qs.stateMap, qs.currentStateCode); }
		return result;
	}
	
	public static Set<String> getQSpriteCodes() { return QSprite.spriteMap.keySet(); }
	
	
	//
	// Instance Methods
	//
	
	//// Accessors
	
	@Override
	public String toString() { return "[" + this.code + "]"; }
	
	
	//// Mutators
	
	public void setCurrentStateCode(String stateCode)
	{
		// set state, animation, and shape only if state is different
		if (this.currentStateCode == stateCode) { return; }
		
		String oldOffsetShapeCode = (this.currentState == null) ? "" : this.currentState.offsetShapeCode;
		
		this.currentState = this.stateMap.get(stateCode);
        if (this.currentState == null) { throw new IllegalArgumentException("No state for stateCode \"" + stateCode + "\""); }
        this.currentStateCode = stateCode;
		Animation referenceAnimation = this.animationMap.get(this.currentState.animationCode); 
		this.currentAnimation = new Animation(referenceAnimation.code, referenceAnimation.repeat, referenceAnimation.frames);
		this.currentAnimation.resetAnimation();
		this.setImageFromCurrentAnimation();
		
		// only redo rectangle and boundaries if shape is different
		if (oldOffsetShapeCode == this.currentState.offsetShapeCode) { return; }
		
		this.currentOffsetShape = this.shapeMap.get(this.currentState.offsetShapeCode);
		this.currentOffset = this.currentOffsetShape.getOffset();
		
		QCollidable currentRectangle = this.currentOffsetShape.shape;
		
		FloatPoint2D p0 = this.getFirstVertex(); 
		if (p0 == null) { p0 = new FloatPoint2D(0.0f, 0.0f); }
		
		currentRectangle.putFirstVertexAt(p0.x, p0.y);
		
		FloatPoint2D[] points = new FloatPoint2D[currentRectangle.points.size()];
        currentRectangle.points.toArray(points);
		
		this.reset();
		
		if (points != null) 
		{
			for (FloatPoint2D pt : points) { this.addPoint((int)pt.x, (int)pt.y); } 
		}
	}
	
	
	public void setImageFromCurrentAnimation() { this.currentAnimation.setImageForQSprite(this); }
	public void advanceAnimationOneClick() { this.currentAnimation.advanceOneClick(this); }
    public void resetAnimation() { this.currentAnimation.resetAnimation(); }
	
	
	public void setPositionByQTile(int x, int y) { this.setPosition(x * QTileset.getTileWidth(), y * QTileset.getTileHeight()); }
    public void setPositionRounded()
    {
        IntPoint2D p = this.getIntPosition();
        this.setPosition(p.x, p.y);
    }
	
	public void resolveCollisionInQMapRoom(QMapRoom qmaproom, boolean checkHorizontal, boolean checkVertical)
	{
		if ((this.lastMove.distance(0, 0) > MIN_MOVE_DISTANCE) && qmaproom.hasTileCollidingWith(this))
		{
            this.setPositionRounded();
			while (qmaproom.hasTileCollidingWith(this)) { this.undoLastMoveUnit(); }
		}
	}
    
    public QMapRoom.MapLink getMapLinkOrNullFrom(QMapRoom qmaproom)
    {
        QMapRoom.MapLink result = qmaproom.getCollidingMapLinkOrNull(this);
        return result;
    }
	
	
	//// Extraclass Mutators
	
	@Override public void drawToContext(DrawingContext ctx, int x, int y)
	{ super.drawToContext(ctx, x - this.currentOffset.x, y - this.currentOffset.y); }
	
	
	//
	// Inner Static Classes
	// 
	
	public static class State
	{
		// Instance Variables
		public final String code;
		public final String animationCode;
		public final String offsetShapeCode;
		
		// Constructor
		public State(String _code, String _offsetShapeCode, String _animationCode) 
		{ this.code = _code; this.offsetShapeCode = _offsetShapeCode; this.animationCode = _animationCode; }
	}
	
	public static class OffsetShape
	{
		// Instance Variables
		public    final String      code;
		protected final IntPoint2D  offset;
		public    final QCollidable shape;
		
		// Constructor
		public OffsetShape(String _code, int _x, int _y, QCollidable _shape)
		{
			this.code   = _code;
			this.offset = new IntPoint2D(_x, _y);
			this.shape  = _shape;
		}
		
		// Instance Methods
		public IntPoint2D getOffset() { return new IntPoint2D(this.offset.x, this.offset.y); }
	}
	
	public static class Animation
	{
		// Inner Static Class
		public static class Frame
		{
			// Instance Fields
			protected final DrawingImage image;
			protected final int          duration;
			protected       int          currentClick = 0;
			// Constructors
			public Frame(Frame frame) { this.image = frame.image; this.duration = frame.duration; }
			public Frame(DrawingImage _image, int _duration) { this.image = _image; this.duration = _duration; }
			// Instance Accessors
			public DrawingImage getImage() { return this.image; }
			public int getDuration() { return this.duration; }
			// Instance Mutators
			public int advanceOneClick() { return ((++this.currentClick ) % this.duration); }
			public void resetFrame() { this.currentClick = 0; }
		}
		
		// Instance Variables
		protected final String      code;
        protected final boolean     repeat;
		protected final List<Frame> frames         = new ArrayList<>();
		protected final int         numberOfFrames;
		protected       int         currentFrame   = 0;
		
		// Constructor
		public Animation(String _code, boolean _repeat, List<Frame> _frames) 
		{
			this.code = _code;
            this.repeat = _repeat;
			for (Frame f : _frames) { this.frames.add(new Frame(f)); }
			this.numberOfFrames = this.frames.size();
		}
		
		// Instance Accessors
		public String getCode() { return this.code; }
		public DrawingImage getCurrentImage() { return this.frames.get(this.currentFrame).getImage(); }
		public int getNumberOfFrames() { return this.numberOfFrames; }
		
		// Instance Mutators
		public void advanceOneClick(QSprite qs) { if (0 == this.frames.get(this.currentFrame).advanceOneClick()) { this.advanceOneFrame(qs); } }
		public void advanceOneFrame(QSprite qs) 
        {
            if (this.repeat || this.currentFrame < (this.numberOfFrames - 1))
            {
                this.currentFrame++;
                if (this.repeat && this.currentFrame == this.numberOfFrames)
                {
                    this.currentFrame = 0;
                }
                this.setImageForQSprite(qs); 
            }
        }
		public void setImageForQSprite(QSprite qs) { qs.image = this.getCurrentImage(); }
		public void resetAnimation() { this.frames.get(this.currentFrame).resetFrame(); this.currentFrame = 0; }
	}
}