/* 
 * Nicholas Saney 
 * 
 * Created: January 28, 2015
 * 
 * AndroidDoubleBufferedUI.java
 * AndroidDoubleBufferedUI class definition
 * 
 */

package chairosoft.android;

import chairosoft.ui.DoubleBufferedUI;
import chairosoft.ui.event.ButtonListener;
import chairosoft.ui.event.ButtonEvent;
import chairosoft.ui.event.ButtonSource;
import chairosoft.ui.event.PointerListener;
import chairosoft.ui.event.PointerEvent;
import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import chairosoft.android.graphics.AndroidDrawingImage;
import chairosoft.android.graphics.AndroidDrawingContext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;

public class AndroidDoubleBufferedUI extends DoubleBufferedUI implements View.OnTouchListener
{
    protected QuadradoLauncherActivity activity = null;
    protected SurfaceView view = null;
    protected SurfaceHolder holder = null;
    protected Bitmap dbImage = null;
    
    private ArrayList<View.OnTouchListener> onTouchListeners = new ArrayList<>();
    
    protected AndroidButtonAdapter androidButtonAdapter = null;
    protected AndroidButtonAdapter.Callback androidButtonAdapterCallback = null;
    protected final Rect destinationFrame = new Rect();
    
    protected AndroidPointerAdapter androidPointerAdapter = null;
    
    @Override 
    protected void doStart()
    {
        this.activity = QuadradoLauncherActivity.getActivity();
        this.view = new SurfaceView(this.activity);
        this.activity.setContentView(this.view);
        this.holder = this.view.getHolder();
        this.view.setOnTouchListener(this);
    }
    
    @Override 
    protected void ensureRenderContext()
    {
        if (this.dbImage == null)
        {
            this.dbImage = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
            this.destinationFrame.set(0, 0, this.width, this.height);
            if (this.dbImage == null) 
            {
                System.err.println("Error in ensureRenderContext(): Unable to create Bitmap."); 
                return; 
            }
        }
        
        if (this.renderContext == null)
        {
            Canvas dbCanvas = new Canvas(this.dbImage);
            this.renderContext = new AndroidDrawingContext(dbCanvas);
        }
    }
    
    @Override 
    public void paintScreen()
    {
        if (this.holder == null) { return; }
        try (AndroidDrawingContext context = new AndroidDrawingContext(this.holder.lockCanvas()))
        {
            Canvas canvas = context.canvas;
            if ((canvas != null) && (this.dbImage != null)) 
            {
                try
                {
                    synchronized (this.holder)
                    {
                        canvas.drawColor(0xff000000);
                        canvas.drawBitmap(this.dbImage, null, this.destinationFrame, null); 
                        
                        if (this.androidButtonAdapter != null)
                        {
                            this.androidButtonAdapter.drawOverlayTo(canvas);
                        }
                    }
                }
                finally
                {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            // else 
            // {
                // String message = String.format("Error in paintScreen(): canvas = %s, this.dbImage = %s", canvas, this.dbImage);
                // Log.e("AndroidDoubleBufferedUI", message);
            // }
        }
        catch (Exception ex) 
        {
            System.err.println("DrawingContext error: " + ex);
        }
    }
    
    
    /* handle multiple touch listeners */
    protected boolean addOnTouchListener(View.OnTouchListener l)
    {
        synchronized (this.onTouchListeners) { return this.onTouchListeners.add(l); }
    }
    
    protected boolean removeOnTouchListener(View.OnTouchListener l)
    {
        synchronized (this.onTouchListeners) { return this.onTouchListeners.remove(l); }
    }
    
    @Override 
    public boolean onTouch(View v, MotionEvent e)
    {
        boolean consumed = false;
        synchronized (this.onTouchListeners)
        {
            for (View.OnTouchListener l : this.onTouchListeners)
            {
                consumed = l.onTouch(v, e);
                if (consumed) { break; }
            }
        }
        return consumed;
    }
    
    
    /* watch for button touches */
    @Override
    public void setButtonListener(ButtonListener _buttonListener)
    {
        super.setButtonListener(_buttonListener);
        
        // remove current adapter
        this.removeOnTouchListener(this.androidButtonAdapter);
        this.holder.removeCallback(this.androidButtonAdapterCallback);
        
        // add new adapter
        if (this.buttonListener != null)
        {
            this.androidButtonAdapter = new AndroidButtonAdapter(this.buttonListener);
            this.androidButtonAdapterCallback = new AndroidButtonAdapter.Callback(this);
            
            this.addOnTouchListener(this.androidButtonAdapter);
            this.holder.addCallback(this.androidButtonAdapterCallback);
        }
    }
    
    /* watch for other pointer touches */
    @Override
    public void setPointerListener(PointerListener _pointerListener)
    {
        super.setPointerListener(_pointerListener);
        
        // remove current adapter
        this.removeOnTouchListener(this.androidPointerAdapter);
        
        // add new adapter
        if (this.pointerListener != null)
        {
            this.androidPointerAdapter = new AndroidPointerAdapter(this.pointerListener);
            this.addOnTouchListener(this.androidPointerAdapter);
        }
    }
    
    @Override 
    public void checkForPause()
    {
        // based on: http://stackoverflow.com/questions/6776327/how-to-pause-resume-thread-in-android
        synchronized (this.activity.pauseLock)
        {
            while (this.activity.isPaused)
            {
                try { this.activity.pauseLock.wait(); }
                catch (InterruptedException ex) { }
            }
        }
    }
}