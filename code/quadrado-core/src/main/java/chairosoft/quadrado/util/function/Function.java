/*
 * Nicholas Saney 
 * 
 * Created: September 28, 2013
 * 
 * Function.java
 * Function interface definition
 */

package chairosoft.quadrado.util.function;

/**
 * Represents a function that accepts one argument and produces a result.
 */
public interface Function<T,R> 
{
    R apply(T t); 
}