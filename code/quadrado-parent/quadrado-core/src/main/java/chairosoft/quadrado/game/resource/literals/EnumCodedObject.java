package chairosoft.quadrado.game.resource.literals;

public interface EnumCodedObject<E extends Enum<E> & EnumLiteral<E>> {
    
    ////// Instance Methods //////
    E getCode();
    
}
