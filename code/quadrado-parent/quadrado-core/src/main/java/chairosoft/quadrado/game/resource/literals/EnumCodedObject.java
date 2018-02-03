package chairosoft.quadrado.game.resource.literals;

public abstract class EnumCodedObject<E extends Enum<E> & EnumLiteral<E>> {
    public final E code;
    public E getCode() { return this.code; }
    protected EnumCodedObject(E _code) {
        this.code = _code;
    }
}
