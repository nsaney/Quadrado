/* 
 * Nicholas Saney 
 * 
 * Created: January 30, 2015
 * 
 * AndroidButtonAdapter.java
 * AndroidButtonAdapter class definition
 * 
 */

package chairosoft.android;

import chairosoft.ui.event.ButtonListener;
import chairosoft.ui.event.ButtonSource;
import chairosoft.ui.event.ButtonEvent;
import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.graphics.Color;

import chairosoft.android.graphics.AndroidDrawingContext;
import chairosoft.android.graphics.AndroidFontLayout;

import chairosoft.quadrado.QCollidable;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AndroidButtonAdapter implements View.OnTouchListener
{
    private final ButtonListener buttonListener;
    private final HashMap<Integer,HashSet<ButtonEvent.Code>> touchButtonsMap = new HashMap<>();
    private final HashMap<ButtonEvent.Code,AndroidOverlayButton> codeButtonMap = new HashMap<>();
    
    public AndroidButtonAdapter(ButtonListener _buttonListener)
    {
        this.buttonListener = _buttonListener;
        this.putNewButtonFor(ButtonEvent.Code.LEFT);
        this.putNewButtonFor(ButtonEvent.Code.RIGHT);
        this.putNewButtonFor(ButtonEvent.Code.UP);
        this.putNewButtonFor(ButtonEvent.Code.DOWN);
        this.putNewButtonFor(ButtonEvent.Code.SELECT);
        this.putNewButtonFor(ButtonEvent.Code.START);
        this.putNewButtonFor(ButtonEvent.Code.A);
        this.putNewButtonFor(ButtonEvent.Code.B);
        this.putNewButtonFor(ButtonEvent.Code.X);
        this.putNewButtonFor(ButtonEvent.Code.Y);
        this.putNewButtonFor(ButtonEvent.Code.DEBUG_9);
    }
    
    protected void putNewButtonFor(ButtonEvent.Code code)
    {
        this.codeButtonMap.put(code, new AndroidOverlayButton(code));
    }
    
    public void setButtonRectangleForCode(ButtonEvent.Code code, int x, int y, int x2, int y2)
    {
        this.setButtonPointsForCode(code,  x, y,  x2, y,  x2, y2,  x, y2);
    }
    
    public void setButtonPointsForCode(ButtonEvent.Code code, int... coordinates) 
    {
        if (coordinates.length % 2 != 0) { throw new IllegalArgumentException("Must have even number of coordinates."); }
        AndroidOverlayButton button = this.codeButtonMap.get(code); 
        if (button == null) { return; }
        button.reset();
        for (int i = 0; i < coordinates.length; i += 2)
        {
            button.addPoint(coordinates[i], coordinates[i + 1]);
        }
    }
    
    public HashSet<ButtonEvent.Code> getButtonsForTouch(float px, float py)
    {
        HashSet<ButtonEvent.Code> result = new HashSet<>();
        for (ButtonEvent.Code code : this.codeButtonMap.keySet())
        {
            AndroidOverlayButton button = this.codeButtonMap.get(code);
            if (button == null) { continue; }
            if (button.containsPoint(px, py))
            {
                result.add(code);
            }
        }
        return result;
    }
    
    @Override
    public boolean onTouch(View view, MotionEvent e)
    {
        boolean consumed = false;
        int action = e.getActionMasked();
        int pointerCount = e.getPointerCount();
        for (int i = 0; i < pointerCount; ++i)
        {
            int id = e.getPointerId(i);
            float x = e.getX(i);
            float y = e.getY(i);
            HashSet<ButtonEvent.Code> codes = this.getButtonsForTouch(x, y);
            consumed = consumed || (codes.size() > 0);
            switch (action)
            {
                case MotionEvent.ACTION_POINTER_DOWN: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_DOWN: 
                    this.touchButtonsPress(id, codes);
                    break;
                
                case MotionEvent.ACTION_POINTER_UP: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_UP:
                    this.touchButtonsRelease(id, codes);
                    break;
                
                case MotionEvent.ACTION_MOVE:
                    HashSet<ButtonEvent.Code> oldCodes = this.touchButtonsMap.get(id); 
                    HashSet<ButtonEvent.Code> codesToRemove = new HashSet<>(); 
                    for (ButtonEvent.Code oldCode : oldCodes)
                    {
                        if (!codes.contains(oldCode))
                        {
                            codesToRemove.add(oldCode);
                        }
                    }
                    this.touchButtonsRelease(id, codesToRemove);
                    this.touchButtonsPress(id, codes);
                    break;
                    
                default:
                    break;
            }
        }
        
        return consumed;
    }
    
    protected void touchButtonsPress(int pointerId, HashSet<ButtonEvent.Code> codesToPress)
    {
        HashSet<ButtonEvent.Code> mappedCodes = this.touchButtonsMap.get(pointerId);
        if (mappedCodes == null)
        {
            mappedCodes = new HashSet<ButtonEvent.Code>();
            this.touchButtonsMap.put(pointerId, mappedCodes);
        }
        for (ButtonEvent.Code codeToPress : codesToPress)
        {
            if (codeToPress == null || codeToPress == ButtonEvent.Code.NONE) { return; }
            mappedCodes.add(codeToPress);
            ButtonEvent be = new ButtonEvent(ButtonSource.KEYBOARD, codeToPress); // TODO: change to something other than KEYBOARD
            this.buttonListener.buttonPressed(be);
        }
    }
    
    protected void touchButtonsRelease(int pointerId, HashSet<ButtonEvent.Code> codesToRemove)
    {
        HashSet<ButtonEvent.Code> oldCodes = this.touchButtonsMap.get(pointerId);
        for (ButtonEvent.Code codeToRemove : codesToRemove)
        {
            if (codeToRemove == null || codeToRemove == ButtonEvent.Code.NONE || !oldCodes.remove(codeToRemove)) { continue; }
            ButtonEvent be = new ButtonEvent(ButtonSource.KEYBOARD, codeToRemove); // TODO: change to something other than KEYBOARD
            this.buttonListener.buttonReleased(be);
        }
    }
    
    public void drawOverlayTo(Canvas canvas)
    {
        for (ButtonEvent.Code code : this.codeButtonMap.keySet())
        {
            AndroidOverlayButton button = this.codeButtonMap.get(code);
            if (button == null) { continue; }
            button.drawOverlayTo(canvas);
        }
    }
    
    
    
    //////////////////////////
    // Static Inner Classes //
    //////////////////////////
    
    public static class AndroidOverlayButton extends QCollidable
    {
        protected float xMin = -1;
        protected float yMin = -1;
        protected float xMax = -1;
        protected float yMax = -1;
        protected final Paint buttonPaint = new Paint();
        protected final Paint textPaint = new Paint();
        public final ButtonEvent.Code code;
        public final Rect textBounds = new Rect();
        protected float textX = -1;
        protected float textY = -1;
        public AndroidOverlayButton(ButtonEvent.Code _code)
        {
            this.code = _code;
            this.textPaint.setColor(Color.CC.SILVER);
            this.textPaint.setStyle(Paint.Style.FILL);
            this.textPaint.getTextBounds(this.code.toString(), 0, this.code.toString().length(), this.textBounds);
        }
        
        public void drawOverlayTo(Canvas canvas)
        {
            if (this.points.size() < 2) { return; }
            Path p = AndroidDrawingContext.setPathFromPolygon(new Path(), this);
            canvas.drawPath(p, this.buttonPaint);
            canvas.drawText(this.code.toString(), this.textX, this.textY, this.textPaint);
        }
        
        @Override 
        public void recalculateSlopes()
        {
            super.recalculateSlopes();
            if (this.points.size() < 2) { return; }
            this.xMin = Float.MAX_VALUE;
            this.yMin = Float.MAX_VALUE;
            this.xMax = 0;
            this.yMax = 0;
            for (FloatPoint2D p : this.points)
            {
                if (p.x < xMin) { xMin = p.x; }
                else if (p.x > xMax) { xMax = p.x; }
                if (p.y < yMin) { yMin = p.y; }
                else if (p.y > yMax) { yMax = p.y; }
            }
            this.buttonPaint.setShader(new LinearGradient(this.xMin, this.yMin, this.xMax, this.yMax, 0x3f7f7f7f, 0x7fffffff, Shader.TileMode.CLAMP));
            this.textX = this.xMin + (((this.xMax - this.xMin) - this.textBounds.right) / 2);
            this.textY = this.yMin + (((this.yMax - this.yMin) + this.textBounds.bottom) / 2);
        }
    }
    
    public static class Callback implements SurfaceHolder.Callback
    {
        public final AndroidDoubleBufferedUI androidDbui;
        private Rect frame = null;
        
        public Callback(AndroidDoubleBufferedUI _androidDbui)
        {
            this.androidDbui = _androidDbui;
        }
        
        @Override 
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            // does nothing
        }
        
        @Override 
        public void surfaceCreated(SurfaceHolder holder)
        {
            // does nothing 
        }
        
        @Override 
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
        {
            this.frame = holder.getSurfaceFrame();
            System.err.printf("frame = (%s, %s, %s, %s)\n", this.frame.left, this.frame.top, this.frame.right, this.frame.bottom);
            
            /////////////////////////////
            // Change destinationFrame //
            /////////////////////////////
            int destinationWidth = this.androidDbui.getWidth();
            int destinationHeight = this.androidDbui.getHeight();
            // if (destinationWidth > width) { destinationHeight = (destinationHeight * width) / destinationWidth; destinationWidth = width; }
            // if (destinationHeight > height) { destinationWidth = (destinationWidth * height) / destinationHeight; destinationHeight = height; }
            Rect destinationFrame = this.androidDbui.destinationFrame;
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
            AndroidButtonAdapter androidButtonAdapter = this.androidDbui.androidButtonAdapter;
            
            // [GAMEPAD MODE]
            // . 0 1 2 3 4 5 6 7 8 9 a
            // 0 . . .^. . . . .X. . .
            // 1 . .<. .>. . .Y. .A. .
            // 2 . . .v. . . . .B. . .
            // 3 . . . .-. . .+. . . .
            // 4 . . . . . . . . .9. .
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
    }
}