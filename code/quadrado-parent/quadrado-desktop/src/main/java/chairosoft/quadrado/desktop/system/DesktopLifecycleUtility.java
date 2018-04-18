/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015
 * 
 * DesktopSystemLifecycleHelpers.java
 * DesktopSystemLifecycleHelpers class definition
 * 
 */

package chairosoft.quadrado.desktop.system;

import chairosoft.quadrado.ui.system.LifecycleUtility;

public class DesktopLifecycleUtility extends LifecycleUtility {
    
    ////// Instance Methods //////
    @Override
    public void addApplicationCloseHook(Thread hook) {
        Runtime.getRuntime().addShutdownHook(hook);
    }
    
    @Override
    public void exitApplication(int exitCode) {
        System.exit(exitCode);
    }
    
}