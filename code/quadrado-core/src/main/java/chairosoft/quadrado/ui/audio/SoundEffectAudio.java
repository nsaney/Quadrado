/* 
 * Nicholas Saney 
 * 
 * Created: April 21, 2015
 * 
 * SoundEffectAudio.java
 * SoundEffectAudio class definition
 * 
 */

package chairosoft.quadrado.ui.audio;

import chairosoft.quadrado.ui.UserInterfaceProvider;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.InputStream;

public abstract class SoundEffectAudio implements Closeable {
    
    ////// Creators //////
    public static SoundEffectAudio create(InputStream sourceStream) {
        if (!sourceStream.markSupported()) {
            sourceStream = new BufferedInputStream(sourceStream);
        }
        return UserInterfaceProvider.get().createSoundEffectAudio(sourceStream);
    }
    
    public static SoundEffectAudio create(String resourceName) {
        InputStream sourceStream = SoundEffectAudio.class.getResourceAsStream(resourceName);
        return SoundEffectAudio.create(sourceStream);
    }
    
    
    ////// Instance Methods //////
    public abstract boolean isValid();
    public abstract void play();
    public abstract void stop();
    
}