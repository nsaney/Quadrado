/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015
 * 
 * AndroidSystemLifecycleHelpers.java
 * AndroidSystemLifecycleHelpers class definition
 * 
 */

package chairosoft.android;

import chairosoft.dependency.Dependencies;
import chairosoft.ui.SystemLifecycleHelpers;
import chairosoft.android.QuadradoLauncherActivity;

import java.io.*; 
import java.net.*;
import java.util.*;

public class AndroidSystemLifecycleHelpers extends SystemLifecycleHelpers
{
    // Initializer
    @Override 
    protected void init() 
    {
        // nothing here now
    }
    
    // Instance Methods
    @Override
    public void addApplicationCloseHook(Thread hook)
    {
        QuadradoLauncherActivity.getActivity().addApplicationCloseHook(hook);
    }
    
    @Override
    public void exitApplication(int exitCode)
    {
        QuadradoLauncherActivity.getActivity().finishAndRemoveTask();
    }
}