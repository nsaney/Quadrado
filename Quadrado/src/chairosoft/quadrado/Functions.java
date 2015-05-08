/*
 * Nicholas Saney 
 * 
 * Created: September 28, 2013
 * Modified: December 29, 2014
 * 
 * Functions.java
 * Functions class definition
 */

package chairosoft.quadrado;

import java.util.*;

/**
 * {@code Functions} class for Quadrado (for compatibility with Java versions before 1.8).
 * 
 * @author Nicholas Saney
 */
public final /*static*/ class Functions
{
    //
    // Constructors
    //
    
    /**
     * Default constructor with {@code private} visibility.
     */
    private Functions() {}
    
    
    //
    // Static Inner Types
    //
    
    public static interface Consumer<T> { void accept(T t); }
    
    public static interface Predicate<T> { boolean test(T t); }
    
    public static interface Function<T,R> { R apply(T t); }
    
    public static class Identity<T> implements Function<T,T> 
    {
        public T apply(T t) { return t; }
    }
    
}