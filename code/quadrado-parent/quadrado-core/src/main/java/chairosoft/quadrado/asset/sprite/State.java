package chairosoft.quadrado.asset.sprite;

import chairosoft.quadrado.asset._literals.EnumCodedObjectImpl;

import java.util.EnumMap;

public class State<
    S extends Enum<S> & StateCodeLiteral<S>,
    B extends Enum<B> & BoundingShapeCodeLiteral<B>,
    A extends Enum<A> & AnimationCodeLiteral<A>
> extends EnumCodedObjectImpl<S>
{
    ////// Instance Fields //////
    public final BoundingShape<B> boundingShape;
    public final Animation<A> animation;
    public final StateTransition<S> stateTransition;
    
    
    ////// Constructor //////
    protected State(
        StateConfig<S, B, A> config,
        EnumMap<B, BoundingShape<B>> shapesByCode,
        EnumMap<A, Animation<A>> animationsByCode
    ) {
        super(config.code);
        this.boundingShape = shapesByCode.get(config.boundingShapeCode);
        this.animation = animationsByCode.get(config.animationCode);
        this.stateTransition = config.stateTransition;
    }
    
}
