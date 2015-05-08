/* 
 * Nicholas Saney 
 * 
 * Created: February 16, 2015
 * 
 * DesktopMultitrackBackgroundAudio.java
 * DesktopMultitrackBackgroundAudio class definition
 * 
 */

package chairosoft.desktop.audio;

import chairosoft.ui.audio.MultitrackBackgroundAudio;
import chairosoft.utils.*;

import java.io.*; 
import java.util.*;
import java.net.*;
import javax.sound.sampled.*;

public class DesktopMultitrackBackgroundAudio extends MultitrackBackgroundAudio
{
    protected AudioFormat mixFormat = null;
    protected SourceDataLine mixLine = null;
    
    @Override 
    protected void init() { }
    
    @Override 
    public void close() 
        throws IOException
    {
        super.close();
        this.mixFormat = null;
        if (this.mixLine != null)
        {
            this.mixLine.stop();
            this.mixLine.close();
            this.mixLine = null;
        }
    }
    
    @Override
    public void postLoad(String[] trackLocations) 
        throws Exception
    {
        // based on: http://stackoverflow.com/questions/6986572/how-to-stream-sound-in-java-without-delay-using-sourcedataline
        
        // create mix format
        this.mixFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED, 
            this.samplesPerSecond, 
            this.bytesPerSample * 8, 
            this.channels, 
            this.bytesPerFrame, 
            this.samplesPerSecond,
            false // always little endian
        );
        
        // create, open, and start mix line
        this.mixLine = AudioSystem.getSourceDataLine(this.mixFormat);
        this.mixLine.open(this.mixFormat, this.mixBufferSize);
        this.mixLine.start();
    }
    
    @Override
    protected BufferProcess getNewBufferProcess() { return new DesktopMultitrackBufferProcess(this); }
    
    @Override
    protected void writeMixedAudio(byte[] b, int off, int len) { this.mixLine.write(b, off, len); }
    
    
    //
    // Static Inner Class
    //
    
    public static class DesktopMultitrackBufferProcess extends BufferProcess
    {
        public DesktopMultitrackBufferProcess(DesktopMultitrackBackgroundAudio _bgAudio)
        {
            super(_bgAudio);
        }
        
        @Override
        protected void setup() { super.setup(); }
        
        @Override
        protected void finish() { super.finish(); }
    }
}