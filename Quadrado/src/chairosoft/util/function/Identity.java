/*
 * Nicholas Saney 
 * 
 * Created: September 28, 2013
 * 
 * Identity.java
 * Identity interface definition
 */

package chairosoft.util.function;

/**
 * Represents a function that accepts one argument and returns the same object as the result.
 */
public class Identity<T> implements Function<T,T> 
{
    public T apply(T t) { return t; }
}