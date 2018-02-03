package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.literals.EnumCodedObject;

public class StateTransition<S extends Enum<S> & StateCodeLiteral<S>> extends EnumCodedObject<S> {
    
    ////// Instance Fields //////
    public final int repeatsBeforeTransition;
    
    
    ////// Constructor //////
    protected StateTransition(S _code, int _repeatsBeforeTransition) {
        super(_code);
        this.repeatsBeforeTransition = _repeatsBeforeTransition;
    }
    
}
