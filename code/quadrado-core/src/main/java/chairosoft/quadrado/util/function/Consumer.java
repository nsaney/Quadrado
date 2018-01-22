/*
 * Nicholas Saney 
 * 
 * Created: September 28, 2013
 * 
 * Consumer.java
 * Consumer interface definition
 */

package chairosoft.quadrado.util.function;

/**
 * Represents an operation that accepts a single input argument and returns no result.
 */
public interface Consumer<T> 
{
    void accept(T t); 
}