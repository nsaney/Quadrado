/* 
 * Nicholas Saney 
 * 
 * Created: January 30, 2015
 * 
 * QuadradoLauncherActivity.java
 * QuadradoLauncherActivity class definition
 * 
 */

package chairosoft.quadrado.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Method;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class QuadradoLauncherActivity extends Activity
{
    public final Class<?> mainClass;
    
    protected int heightPixels;
    public int getHeightPixels() { return this.heightPixels; }
    
    protected int widthPixels;
    public int getWidthPixels() { return this.widthPixels; }
    
    public QuadradoLauncherActivity(Class<?> _mainClass)
    {
        this.mainClass = _mainClass;
    }
    
    public final Object pauseLock = new Object();
    public volatile boolean isPaused = false;
    protected static QuadradoLauncherActivity activity = null;
    public static QuadradoLauncherActivity getActivity() { return QuadradoLauncherActivity.activity; }
    protected ArrayList<Thread> applicationCloseHooks = new ArrayList<>();
    public void addApplicationCloseHook(Thread hook)
    {
        System.err.println("Added hook: " + hook);
        this.applicationCloseHooks.add(hook);
    }
    
    /** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState)
    {
        Log.e("QuadradoLauncherActivity", "Create");
        super.onCreate(savedInstanceState);
        
        // Initialize properties
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        this.heightPixels = Math.min(dm.heightPixels, dm.widthPixels);
        this.widthPixels = Math.max(dm.heightPixels, dm.widthPixels);
        
        // Redirect streams
        System.setErr(new PrintStream(new RedirectedStream("System.err")));
        System.setOut(new PrintStream(new RedirectedStream("System.out")));
        
        // Save current context till we need it
        QuadradoLauncherActivity.activity = this;
        
        // Start service
        this.startService(new Intent(this, QuadradoLauncherService.class));
        
        // Start game-specific code
        try
        {
            Method gameMainMethod = this.mainClass.getMethod("main", String[].class);
            Object[] arguments = { new String[] { } };
            gameMainMethod.invoke(null, arguments);
            //your.app.pkg.name.YourAppClassName.main(new String[0]);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        
        
        // View is set to SurfaceView in AndroidDoubleBufferedUI
    }
    
    @Override 
    public void onRestart()
    {
        Log.e("QuadradoLauncherActivity", "Restart");
        super.onRestart();
    }
    
    @Override 
    public void onStart()
    {
        Log.e("QuadradoLauncherActivity", "Start");
        super.onStart();
    }
    
    @Override 
    public void onResume()
    {
        Log.e("QuadradoLauncherActivity", "Resume");
        super.onResume();
        // based on: http://stackoverflow.com/questions/6776327/how-to-pause-resume-thread-in-android
        synchronized (this.pauseLock)
        {
            this.isPaused = false;
            this.pauseLock.notifyAll();
        }
    }
    
    @Override 
    public void onPause()
    {
        Log.e("QuadradoLauncherActivity", "Pause");
        // based on: http://stackoverflow.com/questions/6776327/how-to-pause-resume-thread-in-android
        synchronized (this.pauseLock)
        {
            this.isPaused = true;
        }
        super.onPause();
    }
    
    @Override 
    public void onStop()
    {
        Log.e("QuadradoLauncherActivity", "Stop");
        super.onStop();
    }
    
    @Override 
    public void onDestroy()
    {
        Log.e("QuadradoLauncherActivity", "Destroy");
        super.onDestroy();
    }
    
    public static class RedirectedStream extends OutputStream
    {
        private String line = "";
        public final String name;
        public RedirectedStream(String _name) { this.name = _name; }
        
        @Override public void write(int b) 
        {
            char c = (char)b; 
            if (c == '\n' || c == '\r') { this.flush(); } 
            else { this.line += c; } 
        }
        
        @Override public void flush() { Log.e(this.name, this.line); this.line = ""; }
    }
}
