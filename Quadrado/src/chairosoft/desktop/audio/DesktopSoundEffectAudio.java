/* 
 * Nicholas Saney 
 * 
 * Created: April 21, 2015
 * 
 * DesktopSoundEffectAudio.java
 * DesktopSoundEffectAudio class definition
 * 
 */

package chairosoft.desktop.audio;

import chairosoft.ui.audio.SoundEffectAudio;

import java.io.IOException; 
import java.io.InputStream; 
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * A sound effect implementation using javax.sound.sampled objects.
 */
public class DesktopSoundEffectAudio extends SoundEffectAudio
{
    // Instance Fields
    protected Clip clip = null;
    public Clip getClip() { return this.clip; }
    
    // Creator/Initializer
    @Override
    protected void init(InputStream sourceStream)
    {
        try
        {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sourceStream);
            this.clip = AudioSystem.getClip();
            this.clip.open(ais);
        }
        catch (Exception ex)
        {
            this.clip = null;
            ex.printStackTrace();
        }
    }
    
    
    // Instance Methods
    
    @Override
    public void close() 
        throws IOException
    {
        this.clip.close();
    }
    
    @Override
    public boolean isValid() { return this.clip != null; }
    
    @Override
    public void play() { this.clip.start(); }
    
    @Override
    public void stop() { this.clip.stop(); this.clip.setFramePosition(0); }
}