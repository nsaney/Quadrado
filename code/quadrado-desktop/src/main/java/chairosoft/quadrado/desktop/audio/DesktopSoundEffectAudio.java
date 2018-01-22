/* 
 * Nicholas Saney 
 * 
 * Created: April 21, 2015
 * 
 * DesktopSoundEffectAudio.java
 * DesktopSoundEffectAudio class definition
 * 
 */

package chairosoft.quadrado.desktop.audio;

import chairosoft.quadrado.ui.audio.SoundEffectAudio;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * A sound effect implementation using javax.sound.sampled objects.
 */
public class DesktopSoundEffectAudio extends SoundEffectAudio {
    
    ////// Instance Properties //////
    protected final Clip clip;
    public Clip getClip() {
        return this.clip;
    }
    
    
    ////// Constructor //////
    public DesktopSoundEffectAudio(InputStream sourceStream) {
        Clip _clip = null;
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sourceStream);
            _clip = AudioSystem.getClip();
            _clip.open(ais);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.clip = _clip;
    }
    
    
    ////// Instance Methods //////
    @Override
    public void close() throws IOException {
        this.clip.close();
    }
    
    @Override
    public boolean isValid() {
        return this.clip != null;
    }
    
    @Override
    public void play() {
        this.clip.start();
    }
    
    @Override
    public void stop() {
        this.clip.stop();
        this.clip.setFramePosition(0);
    }
    
}