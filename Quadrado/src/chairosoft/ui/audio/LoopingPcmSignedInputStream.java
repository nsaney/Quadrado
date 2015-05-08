/* 
 * Nicholas Saney 
 * 
 * Created: May 03, 2015 
 * Modified: May 04, 2015 
 * 
 * LoopingPcmSignedInputStream.java
 * LoopingPcmSignedInputStream class definition
 * 
 */

package chairosoft.ui.audio;

import chairosoft.utils.DirectByteArrayInputStream;
import chairosoft.utils.DirectByteArrayOutputStream;
import chairosoft.utils.Restartable;
import chairosoft.utils.RestartableInputStream;
import chairosoft.utils.ByteConverter;

import java.io.*;

/**
 * A looping, in-memory input stream 
 * for PCM_SIGNED-encoded audio data.
 */
public class LoopingPcmSignedInputStream 
    extends PcmSignedInputStream 
    implements Restartable
{
    //
    // Constants    
    //
    
    
    //
    // Instance Fields
    //
    
    protected final Restartable ris;
    
    public final int channels;
    public final int bytesPerSample;
    public final int samplesPerSecond;
    public final boolean isBigEndian;
    protected final byte[] pcmBuffer;
    protected final ByteArrayInputStream pcmBufferInputStream;
    
    public final long loopPreStartChunkNumber;
    public final long loopStartChunkNumber;
    public final long loopStartByteNumber;
    public final long loopStartByteSkip;
    public final long loopEndChunkNumber;
    public final long loopEndByteNumber;
    public final long loopEndByteLimit;
    protected long lastReadChunkNumber = -1;
    protected boolean lastReadChunkWasLoopEndChunk = false;
    
    
    //
    // Static Methods
    //
    
    
    //
    // Constructor
    //
    
    protected <T extends InputStream & Restartable> 
    LoopingPcmSignedInputStream(T sourceStream, 
                                int _channels, 
                                int _bytesPerSample,
                                int _samplesPerSecond,
                                boolean _isBigEndian, 
                                int _maxBufferSize,
                                long _loopStartMillis,
                                long _loopEndMillis
    )
        throws IOException
    {
        super(sourceStream);
        
        this.ris = sourceStream;
        this.channels = _channels;
        this.bytesPerSample = _bytesPerSample;
        this.samplesPerSecond = _samplesPerSecond;
        this.isBigEndian = _isBigEndian;
        this.pcmBuffer = new byte[_maxBufferSize];
        this.pcmBufferInputStream = new ByteArrayInputStream(this.pcmBuffer);
        
        long _loopStartByteNumber = 0;
        long _loopEndByteNumber = 0;
        if (_loopStartMillis < _loopEndMillis)
        {
            int bytesPerTrackSample = this.channels * this.bytesPerSample;
            _loopStartByteNumber = bytesPerTrackSample * (_loopStartMillis * this.samplesPerSecond / 1000L);
            _loopEndByteNumber = bytesPerTrackSample * (_loopEndMillis * this.samplesPerSecond / 1000L);
        }
        this.loopStartByteNumber = _loopStartByteNumber;
        this.loopStartChunkNumber = this.loopStartByteNumber / this.pcmBuffer.length;
        this.loopStartByteSkip = this.loopStartByteNumber % this.pcmBuffer.length;
        this.loopPreStartChunkNumber = this.loopStartChunkNumber - 1;
        this.loopEndByteNumber = _loopEndByteNumber;
        this.loopEndChunkNumber = this.loopEndByteNumber / this.pcmBuffer.length;
        this.loopEndByteLimit = this.loopEndByteNumber % this.pcmBuffer.length;
        
        // System.out.println("this.loopPreStartChunkNumber = " + this.loopPreStartChunkNumber);
        // System.out.println("this.loopStartChunkNumber = " + this.loopStartChunkNumber);
        // System.out.println("this.loopStartByteNumber = " + this.loopStartByteNumber);
        // System.out.println("this.loopStartByteSkip = " + this.loopStartByteSkip);
        // System.out.println("this.loopEndChunkNumber = " + this.loopEndChunkNumber);
        // System.out.println("this.loopEndByteNumber = " + this.loopEndByteNumber);
        // System.out.println("this.loopEndByteLimit = " + this.loopEndByteLimit);
        // System.out.println("this.pcmBuffer.length = " + this.pcmBuffer.length);
    }
    
    
    //
    // Instance Methods
    //
    
    @Override 
    public void restart() 
        throws IOException 
    {
        this.ris.restart(); 
        this.lastReadChunkNumber = -1; 
        this.bytesAvailable = 0; 
        this.lastReadChunkWasLoopEndChunk = false;
    }
    
    @Override public int getChannels() { return this.channels; }
    @Override public int getBytesPerSample() { return this.bytesPerSample; }
    @Override public int getSamplesPerSecond() { return this.samplesPerSecond; }
    @Override public boolean isBigEndian() { return this.isBigEndian; }
    @Override public int getMaxBufferSize() { return this.pcmBuffer.length; }
    
    @Override protected int readBuffer(byte[] b, int off, int len) throws IOException { return this.pcmBufferInputStream.read(b, off, len); }
    @Override protected void resetBuffer() throws IOException { this.pcmBufferInputStream.reset(); }
    
    @Override protected int decodeAndFillBuffer() throws IOException { return this.in.read(this.pcmBuffer); }
    
    @Override protected boolean ensurePcmBuffer()
        throws IOException
    {
        boolean needToEnsureBuffer = this.bytesAvailable == 0;
        // if (needToEnsureBuffer)
        // {
        // }
        
        boolean bufferEnsured = super.ensurePcmBuffer();
        if (needToEnsureBuffer && bufferEnsured)
        {
            ++this.lastReadChunkNumber;
            
            if (this.lastReadChunkNumber == this.loopPreStartChunkNumber)
            {
                // used the first time the loop start is seen
                this.in.mark(Integer.MAX_VALUE);
                // System.out.printf("Loop Start Marked: this.lastReadChunkNumber = %s \n", this.lastReadChunkNumber);
            }
            else if (this.lastReadChunkWasLoopEndChunk)
            {
                this.pcmBufferInputStream.skip(this.loopStartByteSkip);
                this.bytesAvailable -= this.loopStartByteSkip;
                this.lastReadChunkWasLoopEndChunk = false;
                // System.out.printf("Loop Started: this.lastReadChunkNumber = %s \n", this.lastReadChunkNumber);
            }
            else if (this.lastReadChunkNumber == this.loopEndChunkNumber)
            {
                this.in.reset();
                this.in.mark(Integer.MAX_VALUE);
                this.bytesAvailable = (int)this.loopEndByteLimit;
                this.lastReadChunkNumber = this.loopPreStartChunkNumber;
                this.lastReadChunkWasLoopEndChunk = true;
                // System.out.printf("Loop Ended: this.lastReadChunkNumber = %s \n", this.lastReadChunkNumber);
            }
            
        }
        return bufferEnsured;
    }
}