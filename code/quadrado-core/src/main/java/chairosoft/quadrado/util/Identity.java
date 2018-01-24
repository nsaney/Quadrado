package chairosoft.quadrado.util;

import java.util.function.Function;

/**
 * A function that accepts one argument and returns the same object as the result.
 */
public class Identity<T> implements Function<T, T> {
    
    ////// Constants //////
    public static final Identity<String> STRING = new Identity<>();
    
    
    ////// Instance Methods //////
    @Override
    public T apply(T t) {
        return t;
    }
    
}