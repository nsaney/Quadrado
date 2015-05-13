/*
 * Nicholas Saney 
 * 
 * Created: September 28, 2013
 * 
 * Predicate.java
 * Predicate interface definition
 */

package chairosoft.util.function;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 */
public interface Predicate<T> 
{
    boolean test(T t); 
}