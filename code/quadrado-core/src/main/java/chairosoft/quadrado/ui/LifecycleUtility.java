/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015
 * 
 * SystemLifecycleHelpers.java
 * SystemLifecycleHelpers class definition
 * 
 */

package chairosoft.quadrado.ui;

import java.io.*;

public abstract class LifecycleUtility {
    
    ////// Singleton //////
    private static LifecycleUtility SINGLETON = null;
    
    public static LifecycleUtility get() {
        if (LifecycleUtility.SINGLETON == null) {
            LifecycleUtility.SINGLETON = UserInterfaceProvider.get().createLifecycleUtility();
        }
        return LifecycleUtility.SINGLETON;
    }
    
    
    ////// Constructor //////
    protected LifecycleUtility() {
        // nothing here right now
    }
    
    
    ////// Instance Methods - Abstract //////
    public abstract void addApplicationCloseHook(Thread hook);
    public abstract void exitApplication(int exitCode);
    
    
    ////// Instance Methods - Concrete //////
    public void deleteFileOnExit(final File file) {
        if (file != null) {
            file.deleteOnExit();
            this.addApplicationCloseHook(new Thread(() -> {
                if (!file.delete()) {
                    System.err.println("Could not delete file: " + file);
                }
            }));
        }
    }
    
}