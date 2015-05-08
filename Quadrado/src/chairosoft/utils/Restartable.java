/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015 
 * Modified: April 18, 2015 
 * 
 * Restartable.java
 * Restartable interface definition
 * 
 */

package chairosoft.utils;

import java.io.*;

/**
 * An IO object that can be restarted.
 */
public interface Restartable
{
    void restart() throws IOException;
}