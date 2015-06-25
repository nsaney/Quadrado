/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015
 * 
 * SystemLifecycleHelpers.java
 * SystemLifecycleHelpers class definition
 * 
 */

package chairosoft.ui;

import chairosoft.dependency.Dependencies;

import java.io.*; 
import java.net.*;
import java.util.*;

public abstract class SystemLifecycleHelpers
{
    // Singleton instance
    private static SystemLifecycleHelpers singletonInstance = null;
    public static SystemLifecycleHelpers get()
    {
        if (SystemLifecycleHelpers.singletonInstance == null)
        {
            SystemLifecycleHelpers.singletonInstance = SystemLifecycleHelpers.create();
        }
        return SystemLifecycleHelpers.singletonInstance;
    }
    
    // Creator/Initializer
    protected abstract void init();
    private static SystemLifecycleHelpers create()
    {
        SystemLifecycleHelpers result = Dependencies.getNew(SystemLifecycleHelpers.class);
        result.init();
        return result;
    }
    
    // Instance Methods
    public abstract void addApplicationCloseHook(Thread hook);
    public abstract void exitApplication(int exitCode);
    
    public void deleteFileOnExit(final File file)
    {
        file.deleteOnExit();
        this.addApplicationCloseHook(new Thread()
        {
            @Override public void run()
            {
                if (file != null && file.exists())
                {
                    if (file.delete())
                    {
                        //System.err.println("Deleted file: " + file);
                    }
                    else
                    {
                        System.err.println("Could not delete file: " + file);
                    }
                }
            }
        });
    }
    
}