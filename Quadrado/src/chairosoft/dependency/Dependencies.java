/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * Dependencies.java
 * Dependencies class definition
 * 
 */

package chairosoft.dependency;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class Dependencies
{
    private Dependencies() { }
    
    private static HashMap<Class<?>,Class<?>> map = new HashMap<>();
    
    // TODO: Find a place for this
    static
    {
        Dependencies.register(chairosoft.ui.SystemLifecycleHelpers.class,          chairosoft.desktop.DesktopSystemLifecycleHelpers.class);
        Dependencies.register(chairosoft.quadrado.ui.DoubleBufferedUI.class, chairosoft.quadrado.desktop.DesktopDoubleBufferedUI.class);
        Dependencies.register(chairosoft.ui.graphics.DrawingImage.class,           chairosoft.desktop.graphics.DesktopDrawingImage.class);
        Dependencies.register(chairosoft.ui.audio.MultitrackBackgroundAudio.class, chairosoft.desktop.audio.DesktopMultitrackBackgroundAudio.class);
        Dependencies.register(chairosoft.ui.audio.SoundEffectAudio.class,          chairosoft.desktop.audio.DesktopSoundEffectAudio.class);
    }
    
    
    public static <T, U extends T> 
    void register(Class<T> clazzT, Class<U> clazzU)
    {
        Dependencies.map.put(clazzT, clazzU);
    }
    
    public static <T>
    T getNew(Class<T> clazz)
    {
        T result = null;
        
        @SuppressWarnings("unchecked")
        Class<? extends T> subclass = (Class<? extends T>)Dependencies.map.get(clazz);
        
        if (subclass != null)
        {
            try
            {
                Constructor<? extends T> cons = subclass.getConstructor();
                result = cons.newInstance();
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
        
        return result;
    }
}