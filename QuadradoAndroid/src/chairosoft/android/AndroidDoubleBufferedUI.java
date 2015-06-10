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

import chairosoft.quadrado.ui.DoubleBufferedUI;
import chairosoft.quadrado.ui.event.ButtonListener;
import chairosoft.quadrado.ui.event.ButtonEvent;
import chairosoft.quadrado.ui.event.ButtonSource;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class AndroidDoubleBufferedUI extends DoubleBufferedUI
{
    protected QuadradoLauncherActivity activity = null;
    protected SurfaceView view = null;
    protected SurfaceHolder holder = null;
    protected Bitmap dbImage = null;
    protected AndroidButtonAdapter androidButtonAdapter = null;
    protected final Rect destinationFrame = new Rect();
    
    protected boolean touchListenerAdded = false;
    
    @Override protected void doStart()
    {
        this.activity = QuadradoLauncherActivity.getActivity();
        this.view = new SurfaceView(this.activity);
        this.activity.setContentView(this.view);
        this.holder = this.view.getHolder();
        this.addTouchListener();
    }
    
    @Override protected void ensureRenderContext()
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
    
    @Override public void paintScreen()
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
    
    @Override public void checkForPause()
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
    
    /* watch for screen touches */
    private void addTouchListener()
    {
        if (!this.touchListenerAdded)
        {
            this.androidButtonAdapter = new AndroidButtonAdapter(this.buttonListener);
            this.holder.addCallback(new SurfaceHolder.Callback() 
            {
                private Rect frame = null;
                
                @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
                {
                    this.frame = holder.getSurfaceFrame();
                    System.err.printf("frame = (%s, %s, %s, %s)\n", this.frame.left, this.frame.top, this.frame.right, this.frame.bottom);
                    
                    /////////////////////////////
                    // Change destinationFrame //
                    /////////////////////////////
                    int destinationWidth = AndroidDoubleBufferedUI.this.width;
                    int destinationHeight = AndroidDoubleBufferedUI.this.height;
                    // if (destinationWidth > width) { destinationHeight = (destinationHeight * width) / destinationWidth; destinationWidth = width; }
                    // if (destinationHeight > height) { destinationWidth = (destinationWidth * height) / destinationHeight; destinationHeight = height; }
                    Rect destinationFrame = AndroidDoubleBufferedUI.this.destinationFrame;
                    destinationFrame.set(0, 0, destinationWidth, destinationHeight);
                    int centerOffsetX = (width - destinationWidth) / 2;
                    int centerOffsetY = (height - destinationHeight) / 2;
                    
                    // [GAMEPAD MODE]
                    // top-center
                    destinationFrame.offset(centerOffsetX, Math.min(centerOffsetX, centerOffsetY));
                    
                    // [C MODE]
                    // middle-center 
                    //destinationFrame.offset(centerOffsetX, centerOffsetY); 
                    
                    //System.err.printf("destinationFrame = (%s, %s, %s, %s)\n", destinationFrame.left, destinationFrame.top, destinationFrame.right, destinationFrame.bottom);
                    
                    
                    /////////////////////////
                    // Set overlay buttons //
                    /////////////////////////
                    AndroidButtonAdapter androidButtonAdapter = AndroidDoubleBufferedUI.this.androidButtonAdapter;
                    
                    // [GAMEPAD MODE]
                    // . 0 1 2 3 4 5 6 7 8 9 a
                    // 0 . . .,. . . . .,. . .
                    // 1 . .,. .,. . .,. .,. .
                    // 2 . . .,. . . . .,. . .
                    // 3 . . . .,. . .,. . . .
                    // 4 . . . . . . . . . . .
                    // 5 . . . . . . . . . . .
                    int side = 32;
                    int hs = side / 2;
                    int shs = side + hs;
                    int x1 =         hs; int x2 =   2 * side; int x3 =   3 * side; int x4 = (4 * side) + hs;
                    int x6 = width - x4; int x7 = width - x3; int x8 = width - x2; int x9 = width - x1;
                    int y5 = height - side; 
                    int y4 = y5 - side; int y3 = y4 - side; int y2 = y3 - shs; int y1 = y2 - side; int y0 = y1 - shs;
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.LEFT,  x1, y0, x2, y3);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.RIGHT, x3, y0, x4, y3);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.UP,    x1, y0, x4, y1);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.DOWN,  x1, y2, x4, y3);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.SELECT, x3, y4 + hs, x4, y5);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.START,  x6, y4 + hs, x7, y5);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.A, x8, y1, x9, y2);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.B, x7, y2, x8, y3);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.X, x7, y0, x8, y1);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.Y, x6, y1, x7, y2);
                    androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.DEBUG_9, x8, y4, width, y5);
                    
                    
                    // [C MODE]
                    // . 0 1 2 3
                    // 0 . . . .
                    // 1 . . . .
                    // 2 . . . .
                    // 3 . . . .
                    // int x0 = 0; int x1 = width / 3; int x2 = 2 * x1; int x3 = width;
                    // int y0 = 0; int y1 = height / 3; int y2 = 2 * y1; int y3 = height;
                    // System.err.printf("xn = [%s, %s, %s, %s]\n", x0, x1, x2, x3);
                    // System.err.printf("yn = [%s, %s, %s, %s]\n", y0, y1, y2, y3);
                    // androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.LEFT,  x0, y1, x1, y2);
                    // androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.RIGHT, x2, y1, x3, y2);
                    // androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.UP,    x1, y0, x2, y1);
                    // androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.DOWN,  x1, y2, x2, y3);
                    // androidButtonAdapter.setButtonRectangleForCode(ButtonEvent.Code.START, x2, y2, x3, y3);
                }
                @Override public void surfaceDestroyed(SurfaceHolder holder) { }
                @Override public void surfaceCreated(final SurfaceHolder holder) { }
            });
            this.view.setOnTouchListener(this.androidButtonAdapter);
            this.touchListenerAdded = true;
        }
    }
}