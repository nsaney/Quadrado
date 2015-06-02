/* 
 * Nicholas Saney 
 * 
 * Created: June 01, 2015 
 * 
 * AppendableFileInputStream.java
 * AppendableFileInputStream class definition
 * 
 */

package chairosoft.util;

import java.io.*;

/**
 * A restartable file input stream whose underlying file can be appended to.
 */
public class AppendableFileInputStream extends RestartableFileInputStream
{
    protected Object readWriteLock = new Object();
    protected long readMark = -1;
    
    public AppendableFileInputStream(File file)
        throws IOException, FileNotFoundException
    {
        super(new RandomAccessFile(file, "rw"));
        this.readMark = this.mark;
    }
    
    @Override public void restart() throws IOException { synchronized (this.readWriteLock) { super.restart(); } }
    @Override public int available() throws IOException { synchronized (this.readWriteLock) { return super.available(); } }
    @Override public void close() throws IOException { synchronized (this.readWriteLock) { super.close(); } }
    @Override public void mark(int readLimit) { synchronized (this.readWriteLock) { super.mark(readLimit); } }
    @Override public void reset() throws IOException { synchronized (this.readWriteLock) { super.reset(); } }
    @Override public long skip(long n) throws IOException { synchronized (this.readWriteLock) { return super.skip(n); } }
    @Override public int read() throws IOException { synchronized (this.readWriteLock) { return super.read(); } }
    @Override public int read(byte[] b) throws IOException { synchronized (this.readWriteLock) { return super.read(b); } }
    @Override public int read(byte[] b, int off, int len) throws IOException { synchronized (this.readWriteLock) { return super.read(b, off, len); } }
    
    protected void saveReadPositionAndSeekToEnd()
        throws IOException
    {
        this.readMark = this.raFile.getFilePointer();
        this.raFile.seek(this.raFile.length());
    }
    
    protected void restoreReadPosition()
        throws IOException
    {
        this.raFile.seek(this.readMark);
    }
    
    public void append(int b)
        throws IOException
    {
        synchronized (this.readWriteLock)
        {
            this.saveReadPositionAndSeekToEnd();
            this.raFile.write(b);
            this.restoreReadPosition();
        }
    }
    
    public void append(byte[] b)
        throws IOException
    {
        synchronized (this.readWriteLock)
        {
            this.saveReadPositionAndSeekToEnd();
            this.raFile.write(b);
            this.restoreReadPosition();
        }
    }
    
    public void append(byte[] b, int off, int len)
        throws IOException
    {
        synchronized (this.readWriteLock)
        {
            this.saveReadPositionAndSeekToEnd();
            this.raFile.write(b, off, len);
            this.restoreReadPosition();
        }
    }
}