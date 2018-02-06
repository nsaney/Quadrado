package chairosoft.quadrado.game.resource.literals;

public interface EnumCodedObject<E extends Enum<E> & EnumLiteral<E>> extends Comparable<EnumCodedObject<E>> {
    
    ////// Instance Methods - Abstract //////
    E getCode();
    
    
    ////// Instance Methods - Default //////
    @Override default int compareTo(EnumCodedObject<E> that) {
        return this.getCode().compareTo(that.getCode());
    }
    
}
