/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015 
 * 
 * RestartableInputStream.java
 * RestartableInputStream class definition
 * 
 */

package chairosoft.util;

import java.io.*;

/**
 * A wrapper around an input stream that can be restarted.
 */
public class RestartableInputStream 
    extends FilterInputStream 
    implements Restartable
{
    protected final Restartable restartable;
    
    public <T extends InputStream & Restartable> RestartableInputStream(T sourceStream)
    {
        super(sourceStream);
        this.restartable = sourceStream;
    }
    
    @Override
    public void restart() 
        throws IOException
    {
        this.restartable.restart();
    }
}