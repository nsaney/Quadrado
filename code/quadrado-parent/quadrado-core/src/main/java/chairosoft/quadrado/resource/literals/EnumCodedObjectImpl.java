package chairosoft.quadrado.resource.literals;

public class EnumCodedObjectImpl<E extends Enum<E> & EnumLiteral<E>> implements EnumCodedObject<E> {
    
    ////// Instance Properties //////
    public final E code;
    @Override public E getCode() { return this.code; }
    
    
    ////// Constructor //////
    protected EnumCodedObjectImpl(E _code) {
        this.code = _code;
    }
    
}
