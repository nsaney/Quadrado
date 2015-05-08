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

import java.io.*; 
import java.net.*;
import java.util.*;
import javax.sound.sampled.*;

public class DesktopSoundEffectAudio extends SoundEffectAudio
{
    // Static Fields
    
    static
    {
    }
    
    
    // Instance Fields
    protected Clip clip = null;
    
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