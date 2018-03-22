/* 
 * Nicholas Saney 
 * 
 * Created: January 28, 2015
 * 
 * AndroidDoubleBufferedUI.java
 * AndroidDoubleBufferedUI class definition
 * 
 */

package chairosoft.quadrado.android.system;

import chairosoft.quadrado.android.input.AndroidButtonAdapter;
import chairosoft.quadrado.android.input.AndroidPointerAdapter;
import chairosoft.quadrado.ui.input.ButtonDevice;
import chairosoft.quadrado.ui.system.DoubleBufferedUI;
import chairosoft.quadrado.ui.input.ButtonListener;
import chairosoft.quadrado.ui.input.PointerListener;

import chairosoft.quadrado.android.graphics.AndroidDrawingContext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;

public class AndroidDoubleBufferedUI extends DoubleBufferedUI implements View.OnTouchListener
{
    protected QuadradoLauncherActivity activity = null;
    public QuadradoLauncherActivity getActivity() { return this.activity; }
    
    protected SurfaceView view = null;
    protected SurfaceHolder holder = null;
    protected Bitmap dbImage = null;
    public final Rect destinationFrame = new Rect();
    
    private ArrayList<View.OnTouchListener> onTouchListeners = new ArrayList<>();
    
    protected AndroidButtonAdapter androidButtonAdapter = null;
    protected AndroidButtonAdapter.Callback androidButtonAdapterCallback = null;
    
    protected AndroidPointerAdapter androidPointerAdapter = null;
    
    
    ////// Constructor //////
    public AndroidDoubleBufferedUI(String _title, int _width, int _height, int _xScaling, int _yScaling) {
        super(_title, _width, _height, _xScaling, _yScaling);
    }
    
    
    @Override 
    protected void doStart()
    {
        this.activity = QuadradoLauncherActivity.getActivity();
        this.view = new SurfaceView(this.activity);
        this.activity.setContentView(this.view);
        this.holder = this.view.getHolder();
        this.view.setOnTouchListener(this);
        this.view.setMinimumWidth(Math.min(this.height, this.width));
    }
    
    @Override 
    protected void ensureRenderContext()
    {
        if (this.dbImage == null)
        {
            this.dbImage = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
            this.destinationFrame.set(0, 0, this.activity.getWidthPixels(), this.activity.getHeightPixels());
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
    
    @Override
    protected ButtonDevice chooseButtonDevice() {
        throw new UnsupportedOperationException();
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
    
    
//    /* watch for button touches */
//    @Override
//    public void setButtonListener(ButtonListener _buttonListener)
//    {
//        super.setButtonListener(_buttonListener);
//
//        // remove current adapter
//        this.removeOnTouchListener(this.androidButtonAdapter);
//        this.holder.removeCallback(this.androidButtonAdapterCallback);
//
//        // add new adapter
//        if (this.buttonListener != null)
//        {
//            this.androidButtonAdapter = new AndroidButtonAdapter(this.buttonListener);
//            this.androidButtonAdapterCallback = new AndroidButtonAdapter.Callback(this, this.androidButtonAdapter);
//
//            this.addOnTouchListener(this.androidButtonAdapter);
//            this.holder.addCallback(this.androidButtonAdapterCallback);
//        }
//    }
    
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
            this.androidPointerAdapter = new AndroidPointerAdapter(this);
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