/* 
 * Nicholas Saney 
 * 
 * Created: January 30, 2015
 * 
 * QuadradoLauncherActivity.java
 * QuadradoLauncherActivity class definition
 * 
 */

package chairosoft.android;

import chairosoft.dependency.Dependencies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class QuadradoLauncherActivity extends Activity
{
    public final static String QUADRADO_MAIN_CLASS_NAME_KEY = "QuadradoMainClassName";
    
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
        
        // Redirect streams
        System.setErr(new PrintStream(new RedirectedStream("System.err")));
        System.setOut(new PrintStream(new RedirectedStream("System.out")));
        
        // Override (default) desktop settings
        Dependencies.register(chairosoft.ui.SystemLifecycleHelpers.class,          chairosoft.android.AndroidSystemLifecycleHelpers.class);
        Dependencies.register(chairosoft.quadrado.ui.DoubleBufferedUI.class,       chairosoft.android.AndroidDoubleBufferedUI.class);
        Dependencies.register(chairosoft.ui.graphics.DrawingImage.class,           chairosoft.android.graphics.AndroidDrawingImage.class);
        Dependencies.register(chairosoft.ui.graphics.Font.class,                   chairosoft.android.graphics.AndroidFont.class);
        Dependencies.register(chairosoft.ui.audio.MultitrackBackgroundAudio.class, chairosoft.android.audio.AndroidMultitrackBackgroundAudio.class);
        Dependencies.register(chairosoft.ui.audio.SoundEffectAudio.class,          chairosoft.android.audio.AndroidSoundEffectAudio.class);
        
        // Save current context till we need it
        QuadradoLauncherActivity.activity = this;
        
        // Start service
        this.startService(new Intent(this, QuadradoLauncherService.class));
        
        // Start game-specific code
        try
        {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            String fullyQualifiedClassName = appInfo.metaData.getString(QUADRADO_MAIN_CLASS_NAME_KEY);
            Class<?> gameMainClass = Class.forName(fullyQualifiedClassName);
            Method gameMainMethod = gameMainClass.getMethod("main", String[].class);
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
