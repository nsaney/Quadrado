/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015 
 * 
 * Restartable.java
 * Restartable interface definition
 * 
 */

package chairosoft.util;

import java.io.*;

/**
 * An IO object that can be restarted.
 */
public interface Restartable
{
    void restart() throws IOException;
}