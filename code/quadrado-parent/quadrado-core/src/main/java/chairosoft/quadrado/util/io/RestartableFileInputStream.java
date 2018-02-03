/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015 
 * 
 * RestartableFileInputStream.java
 * RestartableFileInputStream class definition
 * 
 */

package chairosoft.quadrado.util.io;

import java.io.*;

/**
 * A file input stream that can be restarted.
 */
public class RestartableFileInputStream 
    extends InputStream 
    implements Restartable
{
    protected final RandomAccessFile raFile;
    protected final long headerLength;
    protected long mark;
    
    public RestartableFileInputStream(File file)
        throws IOException, FileNotFoundException
    {
        this(new RandomAccessFile(file, "r"));
    }
    
    public RestartableFileInputStream(RandomAccessFile _raFile)
        throws IOException
    {
        this(_raFile, _raFile.getFilePointer());
    }
    
    public RestartableFileInputStream(RandomAccessFile _raFile, long _headerLength)
    {
        this.raFile = _raFile;
        this.headerLength = _headerLength;
        this.mark = this.headerLength;
    }
    
    @Override
    public void restart() 
        throws IOException
    {
        this.raFile.seek(this.headerLength);
    }
    
    @Override
    public int available() 
        throws IOException
    {
        long totalAvailable = this.raFile.length() - this.raFile.getFilePointer();
        return totalAvailable > Integer.MAX_VALUE
            ? Integer.MAX_VALUE
            : (int)totalAvailable;
    }
    
    @Override
    public void close() 
        throws IOException
    {
        this.raFile.close();
    }
    
    @Override
    public void mark(int readlimit)
    {
        try { this.mark = this.raFile.getFilePointer(); }
        catch (IOException ioex) { System.err.println("Error marking RestartableFileInputStream."); ioex.printStackTrace(); }
    }
    
    @Override
    public boolean markSupported()
    {
        return true;
    }
    
    @Override
    public void reset() 
        throws IOException
    {
        this.raFile.seek(this.mark);
    }
    
    @Override
    public long skip(long n) 
        throws IOException
    {
        long totalSkipped = 0;
        int currentSkipped = 0;
        while (n > Integer.MAX_VALUE)
        {
            currentSkipped = this.raFile.skipBytes(Integer.MAX_VALUE);
            if (currentSkipped > 0)
            {
                n -= currentSkipped;
                totalSkipped += currentSkipped;
            }
            else if (currentSkipped == 0)
            {
                return totalSkipped;
            }
            else
            {
                return currentSkipped;
            }
        }
        
        currentSkipped = this.raFile.skipBytes((int)n);
        if (currentSkipped < 0)
        {
            return currentSkipped;
        }
        totalSkipped += currentSkipped;
        
        return totalSkipped;
    }
    
    @Override
    public int read() 
        throws IOException
    {
        return this.raFile.read();
    }
    
    @Override
    public int read(byte[] b) 
        throws IOException
    {
        return this.raFile.read(b);
    }
    
    @Override
    public int read(byte[] b, int off, int len) 
        throws IOException
    {
        return this.raFile.read(b, off, len);
    }
}