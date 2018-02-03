/* 
 * Nicholas Saney 
 * 
 * Created: April 10, 2015
 * 
 * DirectByteArrayInputStream.java
 * DirectByteArrayInputStream class definition
 * 
 */

package chairosoft.quadrado.util.io;

import java.io.*;

/**
 * A byte array input stream with added convenience methods.
 */
public class DirectByteArrayInputStream 
    extends ByteArrayInputStream
    implements Restartable
{
    public DirectByteArrayInputStream(byte[] buf) { super(buf); }
    public DirectByteArrayInputStream(byte[] buf, int offset, int length) { super(buf, offset, length); }
    
    public byte[] getBuffer() { return this.buf; }
    public void markZero() { this.mark = 0; }
    public void restart() throws IOException { this.markZero(); this.reset(); }
}   