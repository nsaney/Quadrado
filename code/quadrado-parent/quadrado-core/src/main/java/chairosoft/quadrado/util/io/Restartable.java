/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015 
 * 
 * Restartable.java
 * Restartable interface definition
 * 
 */

package chairosoft.quadrado.util.io;

import java.io.*;

/**
 * An IO object that can be restarted.
 */
public interface Restartable
{
    void restart() throws IOException;
}