/* 
 * Nicholas Saney 
 * 
 * Created: May 02, 2015 
 * 
 * Mp3ToPcmSignedInputStream.java
 * Mp3ToPcmSignedInputStream class definition
 * 
 */

package chairosoft.ui.audio;

import chairosoft.utils.DirectByteArrayInputStream;
import chairosoft.utils.DirectByteArrayOutputStream;
import chairosoft.utils.Restartable;
import chairosoft.utils.RestartableInputStream;
import chairosoft.utils.ByteConverter;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;

import java.io.*;

/**
 * An input stream that converts from MPEG-encoded audio data 
 * to PCM_SIGNED-encoded audio data.
 */
public class Mp3ToPcmSignedInputStream extends PcmSignedInputStream
{
    //
    // Constants    
    //
    
    public static final int BYTES_PER_SAMPLE = 2;
    public static final boolean IS_BIG_ENDIAN = false;
    public static final ByteConverter BYTE_CONVERTER = new ByteConverter(BYTES_PER_SAMPLE, IS_BIG_ENDIAN);
    
    
    //
    // Instance Fields
    //
    
    protected final BufferPlayer player;
    protected final BufferAudioDevice device;
    protected final Decoder decoder;
    
    
    //
    // Static Methods
    //
    
    public static Mp3ToPcmSignedInputStream getFrom(InputStream sourceStream)
        throws IOException
    {
        return new Mp3ToPcmSignedInputStream(sourceStream);
    }
    
    
    //
    // Constructor
    //
    
    protected Mp3ToPcmSignedInputStream(InputStream sourceStream)
        throws IOException
    {
        super(sourceStream);
        
        // initialize device and player
        this.device = new BufferAudioDevice();
        try { this.player = new BufferPlayer(sourceStream, this.device); }
        catch (JavaLayerException jlex) { throw new IOException("Unable to initialize JLayer MP3 Player object.", jlex); }
        
        // initialize decoder by decoding first buffer
        this.ensurePcmBuffer();
        this.decoder = this.device.getDecoder();
    }
    
    
    //
    // Instance Methods
    //
    
    @Override public void close() throws IOException { super.close(); this.player.close(); }
    
    @Override public int getChannels() { return this.decoder.getOutputChannels(); }
    @Override public int getBytesPerSample() { return Mp3ToPcmSignedInputStream.BYTES_PER_SAMPLE; }
    @Override public int getSamplesPerSecond() { return this.decoder.getOutputFrequency(); }
    @Override public boolean isBigEndian() { return Mp3ToPcmSignedInputStream.IS_BIG_ENDIAN; }
    
    @Override public int getMaxBufferSize() { return this.device.getMaxBufferSize(); }
    @Override protected int readBuffer(byte[] b, int off, int len) throws IOException { return this.device.read(b, off, len); }
    @Override protected void resetBuffer() throws IOException { this.device.reset(); }
    
    @Override 
    protected int decodeAndFillBuffer()
        throws IOException
    {
        boolean success = false;
        try
        {
            success = this.player.decodeOneFrame();
        }
        catch (JavaLayerException jlex)
        {
            throw new IOException(jlex);
        }
        return success ? this.device.getCurrentBytesPerFrame() : -1;
    }
    
    
    //
    // Static Nested Classes
    //
    
    public static class BufferPlayer extends Player
    {
        public BufferPlayer(InputStream stream, AudioDevice device) throws JavaLayerException
        { super(stream, device); }
        
        public boolean decodeOneFrame() throws JavaLayerException { return this.decodeFrame(); }
    }
    
    
    public static class BufferAudioDevice implements AudioDevice
    {
        protected Decoder decoder = null;
        public Decoder getDecoder() { return this.decoder; }
        
        protected byte[] pcmBuffer = null;
        public int getMaxBufferSize() { return this.pcmBuffer == null ? 0 : this.pcmBuffer.length; }
        
        protected ByteArrayInputStream pcmBufferInputStream = null;
        public int read(byte[] b, int off, int len) throws IOException { return this.pcmBufferInputStream.read(b, off, len); }
        public void reset() throws IOException { this.pcmBufferInputStream.reset(); }
        
        protected int maxSamplesPerFrame = 0;
        protected int currentBytesPerFrame = 0;
        public int getCurrentBytesPerFrame() { return this.currentBytesPerFrame; }
        
        @Override 
        public boolean isOpen() { return this.pcmBufferInputStream != null; }
        
        @Override 
        public void open(Decoder decoder)
        {
            if (this.isOpen()) { return; }
            this.decoder = decoder;
            this.maxSamplesPerFrame = this.decoder.getOutputBlockSize();
            int bytesRequired = this.maxSamplesPerFrame * BYTES_PER_SAMPLE;
            this.pcmBuffer = new byte[bytesRequired]; 
            // System.out.printf("bytesRequired = %s = %s * %s \n", bytesRequired, this.maxSamplesPerFrame, BYTES_PER_SAMPLE);
            this.pcmBufferInputStream = new ByteArrayInputStream(this.pcmBuffer);
        }
        
        @Override 
        public void close() 
        {
            if (!this.isOpen()) { return; }
            this.currentBytesPerFrame = 0;
            this.maxSamplesPerFrame = 0;
            this.pcmBuffer = null;
            this.pcmBufferInputStream = null;
            this.decoder = null;
        }
        
        @Override public int getPosition() { return 0; }
        @Override public void flush() { }
        
        @Override 
        public void write(short[] samples, int off, int len)
        {
            int end = Math.min(off + len, this.maxSamplesPerFrame); 
            end = Math.min(end, samples.length);
            int frameBytesOffset = 0;
            for (int i = off; i < end; ++i, frameBytesOffset += BYTES_PER_SAMPLE)
            {
                BYTE_CONVERTER.writeInteger(samples[i], this.pcmBuffer, frameBytesOffset);
            }
            this.currentBytesPerFrame = frameBytesOffset;
        }
    }
}