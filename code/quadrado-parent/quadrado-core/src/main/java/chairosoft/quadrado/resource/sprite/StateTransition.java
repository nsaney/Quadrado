package chairosoft.quadrado.resource.sprite;

import chairosoft.quadrado.resource.literals.EnumCodedObjectImpl;

public class StateTransition<S extends Enum<S> & StateCodeLiteral<S>> extends EnumCodedObjectImpl<S> {
    
    ////// Instance Fields //////
    public final int repeatsBeforeTransition;
    
    
    ////// Constructor //////
    protected StateTransition(S _code, int _repeatsBeforeTransition) {
        super(_code);
        this.repeatsBeforeTransition = _repeatsBeforeTransition;
    }
    
}
