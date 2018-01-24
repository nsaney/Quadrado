/* 
 * Nicholas Saney 
 * 
 * Created: April 18, 2015
 * 
 * AndroidSystemLifecycleHelpers.java
 * AndroidSystemLifecycleHelpers class definition
 * 
 */

package chairosoft.quadrado.android.system;

import chairosoft.quadrado.ui.system.LifecycleUtility;

public class AndroidLifecycleUtility extends LifecycleUtility {
    
    ////// Instance Methods //////
    @Override
    public void addApplicationCloseHook(Thread hook) {
        QuadradoLauncherActivity.getActivity().addApplicationCloseHook(hook);
    }
    
    @Override
    public void exitApplication(int exitCode) {
        QuadradoLauncherActivity.getActivity().finishAndRemoveTask();
    }
    
}