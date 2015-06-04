/* 
 * Nicholas Saney 
 * 
 * Created: February 20, 2015
 * 
 * AndroidMultitrackBackgroundAudio.java
 * AndroidMultitrackBackgroundAudio class definition
 * 
 */

package chairosoft.android.audio;

import chairosoft.android.QuadradoLauncherActivity;
import chairosoft.ui.audio.MultitrackBackgroundAudio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.*; 
import java.util.*;
import java.net.*;

public class AndroidMultitrackBackgroundAudio extends MultitrackBackgroundAudio
{
    protected AudioTrack mixAudioTrack = null;
    
    @Override 
    protected void init() 
    {
        // nothing for now
    }
    
    @Override 
    public void close() 
        throws IOException
    {
        super.close();
        if (this.mixAudioTrack != null)
        {
            this.mixAudioTrack.stop();
            this.mixAudioTrack.release();
            this.mixAudioTrack = null;
        }
    }
    
    @Override 
    public void postLoad(String[] trackLocations) 
        throws Exception
    {
        // based on: http://developer.android.com/reference/android/media/AudioTrack.html#AudioTrack(int, int, int, int, int, int)
        
        int channelConfig = this.channels == 2
            ? AudioFormat.CHANNEL_OUT_STEREO
            : AudioFormat.CHANNEL_OUT_MONO; // uses CHANNEL_OUT_MONO as default
        
        int audioFormat = this.bytesPerSample == 2
            ? AudioFormat.ENCODING_PCM_16BIT
            : AudioFormat.ENCODING_PCM_8BIT; // uses ENCODING_PCM_8BIT as default
        
        // create and play streaming AudioTrack
        this.mixAudioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            this.samplesPerSecond,
            channelConfig,
            audioFormat,
            this.mixBufferSize,
            AudioTrack.MODE_STREAM
        );
        this.mixAudioTrack.play();
    }
    
    @Override
    protected BufferProcess getNewBufferProcess() { return new AndroidMultitrackBufferProcess(this); }
    
    @Override
    protected void writeMixedAudio(byte[] b, int off, int len) { this.mixAudioTrack.write(b, off, len); }
    
    
    //
    // Static Inner Classes
    //
    
    public static class AndroidMultitrackBufferProcess extends BufferProcess
    {
        public AndroidMultitrackBufferProcess(AndroidMultitrackBackgroundAudio _bgAudio)
        {
            super(_bgAudio);
        }
        
        @Override
        protected void setup() { super.setup(); }
        
        @Override
        protected void finish() { super.finish(); }
    }
}