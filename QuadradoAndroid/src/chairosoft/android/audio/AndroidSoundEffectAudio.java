/* 
 * Nicholas Saney 
 * 
 * Created: April 21, 2015
 * 
 * AndroidSoundEffectAudio.java
 * AndroidSoundEffectAudio class definition
 * 
 */

package chairosoft.android.audio;

import chairosoft.ui.audio.SoundEffectAudio;
import chairosoft.util.*;

import android.speech.srec.WaveHeader;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.*; 
import java.net.*;
import java.util.*;

public class AndroidSoundEffectAudio extends SoundEffectAudio
{
    // Static Fields
    
    static
    {
    }
    
    
    // Instance Fields
    protected AudioTrack audioTrack = null;
    
    // Creator/Initializer
    @Override
    protected void init(InputStream sourceStream)
    {
        try (InputStream in = sourceStream)
        {
            WaveHeader waveHeader = new WaveHeader();
            waveHeader.read(in);
            
            int bufferSize = waveHeader.getNumBytes();
            if (bufferSize <= 0) { bufferSize = 32 * 1024; }
            
            byte[] readBuffer = new byte[bufferSize];
            DirectByteArrayOutputStream dbaos = new DirectByteArrayOutputStream();
            int currentRead = 0;
            while ((currentRead = in.read(readBuffer)) > 0)
            {
                dbaos.write(readBuffer, 0, currentRead);
            }
            
            byte[] data = dbaos.getBuffer();
            
            int channelConfig = waveHeader.getNumChannels() == 2
                ? AudioFormat.CHANNEL_OUT_STEREO
                : AudioFormat.CHANNEL_OUT_MONO; // uses CHANNEL_OUT_MONO as default
            
            int audioFormat = waveHeader.getBitsPerSample() == 16
                ? AudioFormat.ENCODING_PCM_16BIT
                : AudioFormat.ENCODING_PCM_8BIT; // uses ENCODING_PCM_8BIT as default
            
            // create static AudioTrack
            this.audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                waveHeader.getSampleRate(),
                channelConfig,
                audioFormat,
                data.length,
                AudioTrack.MODE_STATIC
            );
            
            this.audioTrack.write(data, 0, data.length);
        }
        catch (Exception ex)
        {
            this.audioTrack = null;
            ex.printStackTrace();
        }
    }
    
    
    // Instance Methods
    
    @Override
    public void close() 
        throws IOException
    {
        this.audioTrack.release();
    }
    
    @Override
    public boolean isValid() { return this.audioTrack != null; }
    
    @Override
    public void play() { this.audioTrack.play(); }
    
    @Override
    public void stop() { this.audioTrack.stop(); this.audioTrack.setPlaybackHeadPosition(0); }
}