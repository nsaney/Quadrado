package chairosoft.quadrado.resource.sprite;

import chairosoft.quadrado.element.QCollidable;
import chairosoft.quadrado.element.QPhysical2D;
import chairosoft.quadrado.resource.literals.EnumCodedObject;
import chairosoft.quadrado.resource.maproom.QMapRoom;
import chairosoft.quadrado.resource.tileset.QTileset;
import chairosoft.quadrado.ui.geom.FloatPoint2D;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.graphics.DrawingContext;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

// TODO: add documentation
public abstract class QSprite<
    S extends Enum<S> & StateCodeLiteral<S>,
    B extends Enum<B> & BoundingShapeCodeLiteral<B>,
    A extends Enum<A> & AnimationCodeLiteral<A>
>
    extends QPhysical2D
{
    
    ////// Instance Fields //////
    public final SpriteSheet spriteSheet;
    public final EnumMap<B, BoundingShape<B>> boundingShapesByCode;
    public final EnumMap<A, Animation<A>> animationsByCode;
    public final EnumMap<S, State<S, B, A>> statesByCode;
    
    protected State<S, B, A> currentState = null;
    protected Animation<A> currentAnimation = null;
    protected BoundingShape<B> currentBoundingShape = null;
    
    
    ////// Constructor //////
    protected QSprite(
        SpriteSheetConfig spriteSheetConfig,
        List<BoundingShape<B>> boundingShapes,
        List<Animation<A>> animations,
        List<StateConfig<S, B, A>> stateConfigs
    ) {
        this.spriteSheet = SpriteSheet.loadFor(spriteSheetConfig);
        this.boundingShapesByCode = EnumCodedObject.toMap(boundingShapes);
        this.animationsByCode = EnumCodedObject.toMap(animations);
        this.statesByCode = EnumCodedObject.toMapWithValues(
            stateConfigs,
            stateConfig -> new State<>(stateConfig, this.boundingShapesByCode, this.animationsByCode)
        );
        S defaultStateCode = stateConfigs.get(0).code;
        this.setCurrentStateCode(defaultStateCode);
    }
    
    
    ////// Instance Methods //////
    @Override
    public String toString() {
        return "[" + this.getClass() + "]";
    }
    
    @Override
    public void drawToContext(DrawingContext ctx, int x, int y) {
        int xOffset = this.currentBoundingShape.offset.x;
        int yOffset = this.currentBoundingShape.offset.y;
        super.drawToContext(ctx, x - xOffset, y - yOffset);
    }
    
    public void advanceAnimationOneClick() {
        int currentRepeatCount = this.currentAnimation.advanceOneClickAndGetRepeatCount();
        StateTransition<S> currentStateTransition = this.currentState.stateTransition;
        if (currentStateTransition != null && currentRepeatCount > currentStateTransition.repeatsBeforeTransition) {
            // TODO: set state
        }
        else {
            this.setImageFromCurrentAnimationFrame();
        }
    }
    
    public void setImageFromCurrentAnimationFrame() {
        AnimationFrame currentFrame = this.currentAnimation.getCurrentFrame();
        if (currentFrame.currentClick == 0) {
            int currentImageIndex = currentFrame.imageIndex;
            this.image = this.spriteSheet.getImage(currentImageIndex);
        }
    }
    
    public void resetAnimationAndImage() {
        this.currentAnimation.reset();
        this.setImageFromCurrentAnimationFrame();
    }
    
    public void setCurrentStateCode(S stateCode) {
        // set state, animation, and shape only if state is different
        if (this.currentState != null && this.currentState.code == stateCode) { return; }
        
        B oldBoundingShapeCode = this.currentBoundingShape == null ? null : this.currentBoundingShape.code;
        
        this.currentState = this.statesByCode.get(stateCode);
        if (this.currentState == null) {
            String message = String.format("No state for stateCode [%s]", stateCode);
            throw new IllegalArgumentException(message);
        }
        this.currentAnimation = this.currentState.animation;
        this.resetAnimationAndImage();
        
        // only redo boundaries if necessary
        this.currentBoundingShape = this.currentState.boundingShape;
        if (oldBoundingShapeCode == this.currentBoundingShape.code) { return; }
        
        QCollidable currentShape = this.currentBoundingShape.shape;
        FloatPoint2D p0 = this.getFirstVertex();
        if (p0 == null) {
            p0 = new FloatPoint2D(0.0f, 0.0f);
        }
        currentShape.putFirstVertexAt(p0.x, p0.y);
        
        this.reset();
        this.addAllPointsAsInt(currentShape.points);
    }
    
    public void setPositionByQTile(QMapRoom<?> mapRoom, int x, int y) {
        QTileset<?> tileset = mapRoom.tileset;
        this.setPosition(
            x * tileset.tileWidth,
            y * tileset.tileHeight
        );
    }
    
    public void setPositionRounded() {
        IntPoint2D p = this.getIntPosition();
        this.setPosition(p.x, p.y);
    }
    
    
    ////// Static Methods - Declarative Syntax //////
    @SafeVarargs
    protected static <B extends Enum<B> & BoundingShapeCodeLiteral<B>>
    List<BoundingShape<B>> shapes(BoundingShape<B>... shapesArray) {
        return Arrays.asList(shapesArray);
    }
    
    protected static <B extends Enum<B> & BoundingShapeCodeLiteral<B>>
    BoundingShape<B> shape(B _code, int _x, int _y, QCollidable _shape) {
        return new BoundingShape<>(_code, _x, _y, _shape);
    }
    
    protected static <B extends Enum<B> & BoundingShapeCodeLiteral<B>>
    BoundingShape<B> shape(B _code, int _x, int _y, int _width, int _height) {
        return shape(_code, _x, _y, new QCollidable(0, 0, _width, _height));
    }
    
    @SafeVarargs
    protected static <A extends Enum<A> & AnimationCodeLiteral<A>>
    List<Animation<A>> animations(Animation<A>... animationsArray) {
        return Arrays.asList(animationsArray);
    }
    
    protected static <A extends Enum<A> & AnimationCodeLiteral<A>>
    Animation<A> animation(A _code, AnimationFrame... _frames) {
        return new Animation<>(_code, _frames);
    }
    
    protected static AnimationFrame frame(int _imageIndex, int _duration) {
        return new AnimationFrame(_imageIndex, _duration);
    }
    
    @SafeVarargs
    protected static <
        S extends Enum<S> & StateCodeLiteral<S>,
        B extends Enum<B> & BoundingShapeCodeLiteral<B>,
        A extends Enum<A> & AnimationCodeLiteral<A>
    > List<StateConfig<S, B, A>> states(StateConfig<S, B, A>... statesArray) {
        return Arrays.asList(statesArray);
    }
    
    protected static <
        S extends Enum<S> & StateCodeLiteral<S>,
        B extends Enum<B> & BoundingShapeCodeLiteral<B>,
        A extends Enum<A> & AnimationCodeLiteral<A>
    > StateConfig<S, B, A> state(S _stateCode, B _boundingShapeCode, A _animationCode, StateTransition<S> _stateTransition) {
        return new StateConfig<>(_stateCode, _boundingShapeCode, _animationCode, _stateTransition);
    }
    
    protected static <
        S extends Enum<S> & StateCodeLiteral<S>,
        B extends Enum<B> & BoundingShapeCodeLiteral<B>,
        A extends Enum<A> & AnimationCodeLiteral<A>
    > StateConfig<S, B, A> state(S _stateCode, B _boundingShapeCode, A _animationCode) {
        return state(_stateCode, _boundingShapeCode, _animationCode, null);
    }
    
}
