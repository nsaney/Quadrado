/* 
 * Nicholas Saney 
 * 
 * Created: April 19, 2015
 * 
 * QuadradoLauncherService.java
 * QuadradoLauncherService class definition
 * 
 */

package chairosoft.quadrado.android.system;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class QuadradoLauncherService extends Service
{
    @Override
    public void onCreate()
    {
        System.err.println("Created service: " + this);
        super.onCreate();
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    
    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        System.err.println("Task Removed for service: " + this);
        QuadradoLauncherActivity activity = QuadradoLauncherActivity.getActivity();
        System.err.println("Started running hooks: " + activity.applicationCloseHooks);
        try
        {
            for (Thread hook : activity.applicationCloseHooks)
            {
                System.err.println("Running hook: " + hook);
                hook.run();
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
        System.err.println("Done running hooks.");
        super.onTaskRemoved(rootIntent);
    }
}