/* 
 * Nicholas Saney 
 * 
 * Created: May 02, 2015 
 * 
 * PcmSignedInputStream.java
 * PcmSignedInputStream abstract class definition
 * 
 */

package chairosoft.quadrado.ui.audio;

import java.io.*;

/**
 * A filter input stream that provides PCM_SIGNED-encoded audio data
 * based on the data in the source stream.
 */
public abstract class PcmSignedInputStream extends FilterInputStream
{
    // Instance Fields
    private final byte[] bufferOfMinimalism = new byte[1];
    protected int bytesAvailable = 0;
    
    // Constructors
    public PcmSignedInputStream(InputStream sourceStream)
    {
        super(sourceStream);
    }
    
    // Instance Methods
    @Override public int available() { return this.bytesAvailable; }
    @Override public void mark(int readlimit) { } // do nothing
    @Override public boolean markSupported() { return false; }
    @Override public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
    @Override public long skip(long n) throws IOException { throw new IOException("skip/seek not supported"); }
    
    public abstract int getChannels();
    public abstract int getBytesPerSample();
    public abstract int getSamplesPerSecond();
    public abstract boolean isBigEndian();
    public abstract int getMaxBufferSize();
    
    /**
     * Performs InputStream.read() on the implementing 
     * class's buffer of PCM signed data.
     * 
     * @param b   the byte array to read into
     * @param off the offset to use when filling the buffer
     * @param len the number of bytes to attempt to read
     * 
     * @return the number of PCM signed bytes actually read
     * @throws IOException if an error occurs during the read
     */
    protected abstract int readBuffer(byte[] b, int off, int len) throws IOException;
    
    /**
     * Performs InputStream.reset() on the implementing 
     * class's buffer of PCM signed data.
     * @throws IOException if an error occurs during the reset
     */
    protected abstract void resetBuffer() throws IOException;
    
    /**
     * Decodes a single "chunk" of audio data in the source stream 
     * and stores the data in the implementing class's buffer 
     * of PCM signed data, returning the number of PCM signed bytes decoded.
     * 
     * @return the number of PCM signed bytes decoded, 
     *         or -1 if the end of the source stream was reached
     * @throws IOException if an error occurs during the decode
     */
    protected abstract int decodeAndFillBuffer() throws IOException;
    
    protected boolean ensurePcmBuffer()
        throws IOException
    {
        if (this.bytesAvailable < 0)
        {
            throw new IllegalStateException("Count of available bytes cannot fall below zero: " + this.bytesAvailable);
        }
        
        boolean bufferEnsured = this.bytesAvailable > 0;
        if (!bufferEnsured)
        {
            this.resetBuffer();
            int pcmBytesDecoded = this.decodeAndFillBuffer();
            if (pcmBytesDecoded > 0)
            {
                bufferEnsured = true;
                this.bytesAvailable = pcmBytesDecoded;
            }
        }
        return bufferEnsured;
    }
    
    @Override
    public int read() 
        throws IOException
    {
        int result = this.read(bufferOfMinimalism);
        if (result == 1) { result = bufferOfMinimalism[0]; }
        return result;
    }
    
    @Override
    public int read(byte[] b) 
        throws IOException
    {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(byte[] b, int off, int len) 
        throws IOException
    {
        //System.err.printf("\n  len = %s and this.bytesAvailable = %s ... ", len, this.bytesAvailable);
        
        int pcmBytesRead = 0;
        boolean bufferEnsured = this.ensurePcmBuffer();
        if (!bufferEnsured) { return -1; }
        
        int currentRead = 0;
        while (0 < this.bytesAvailable && this.bytesAvailable < len)
        {
            currentRead = this.readBuffer(b, off, this.bytesAvailable);
            //System.err.printf("\n   %s = this.readBuffer(b, %s, %s); // for a while. ", currentRead, off, this.bytesAvailable);
            pcmBytesRead += currentRead;
            this.bytesAvailable -= currentRead;
            off += currentRead;
            len -= currentRead;
            
            this.ensurePcmBuffer();
        }
        
        if (this.bytesAvailable < len) { len = this.bytesAvailable; }
        currentRead = this.readBuffer(b, off, len);
        //System.err.printf("\n   %s = this.readBuffer(b, %s, %s); // once. \n", currentRead, off, len);
        pcmBytesRead += currentRead;
        this.bytesAvailable -= currentRead;
        return pcmBytesRead;
    }
}