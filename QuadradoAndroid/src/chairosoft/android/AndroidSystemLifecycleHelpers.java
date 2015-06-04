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
    protected void init() { }
    
    // Instance Methods
    public void addApplicationCloseHook(Thread hook)
    {
        QuadradoLauncherActivity.getActivity().addApplicationCloseHook(hook);
    }
}