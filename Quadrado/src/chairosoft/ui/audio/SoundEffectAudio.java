/* 
 * Nicholas Saney 
 * 
 * Created: April 21, 2015
 * Modified: April 21, 2015
 * 
 * SoundEffectAudio.java
 * SoundEffectAudio class definition
 * 
 */

package chairosoft.ui.audio;

import chairosoft.dependency.Dependencies;
import chairosoft.ui.SystemLifecycleHelpers;
import chairosoft.utils.*;

import java.io.*; 
import java.net.*;
import java.util.*;

public abstract class SoundEffectAudio implements Closeable
{
    // Static Fields
    
    static
    {
    }
    
    
    // Instance Fields
    
    
    // Creator/Initializer
    protected abstract void init(InputStream sourceStream);
    public static SoundEffectAudio create(InputStream sourceStream)
    {
        SoundEffectAudio result = Dependencies.getNew(SoundEffectAudio.class);
        if (!sourceStream.markSupported())
        {
            sourceStream = new BufferedInputStream(sourceStream);
        }
        result.init(sourceStream);
        return result;
    }
    
    public static SoundEffectAudio create(String resourceName)
    {
        InputStream sourceStream = SoundEffectAudio.class.getResourceAsStream(resourceName);
        return SoundEffectAudio.create(sourceStream);
    }
    
    
    // Instance Methods
    public abstract boolean isValid();
    public abstract void play();
    public abstract void stop();
}