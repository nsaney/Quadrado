/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015
 * Modified: April 18, 2015
 * 
 * DesktopSystemLifecycleHelpers.java
 * DesktopSystemLifecycleHelpers class definition
 * 
 */

package chairosoft.desktop;

import chairosoft.dependency.Dependencies;
import chairosoft.ui.SystemLifecycleHelpers;

import java.io.*; 
import java.net.*;
import java.util.*;

public class DesktopSystemLifecycleHelpers extends SystemLifecycleHelpers
{
    // Initializer
    protected void init() { }
    
    // Instance Methods
    public void addApplicationCloseHook(Thread hook)
    {
        Runtime.getRuntime().addShutdownHook(hook);
    }
}