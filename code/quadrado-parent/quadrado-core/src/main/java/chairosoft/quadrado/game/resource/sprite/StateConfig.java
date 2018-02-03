package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.literals.EnumCodedObject;

public class StateConfig<
    S extends Enum<S> & StateCodeLiteral<S>,
    B extends Enum<B> & BoundingShapeCodeLiteral<B>,
    A extends Enum<A> & AnimationCodeLiteral<A>
> extends EnumCodedObject<S>
{
    
    ////// Instance Fields //////
    public final B boundingShapeCode;
    public final A animationCode;
    public final StateTransition<S> stateTransition;
    
    
    ////// Constructor //////
    public StateConfig(S _stateCode, B _boundingShapeCode, A _animationCode, StateTransition<S> _stateTransition) {
        super(_stateCode);
        this.boundingShapeCode = _boundingShapeCode;
        this.animationCode = _animationCode;
        this.stateTransition = _stateTransition;
    }
    
}
