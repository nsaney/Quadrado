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

import chairosoft.quadrado.ui.event.ButtonListener;
import chairosoft.quadrado.ui.event.ButtonSource;
import chairosoft.quadrado.ui.event.ButtonEvent;

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
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AndroidButtonAdapter implements View.OnTouchListener
{
    private final ButtonListener buttonListener;
    private final HashMap<Integer,HashSet<ButtonEvent.Code>> touchButtonsMap = new HashMap<>();
    private final HashMap<ButtonEvent.Code,AndroidOverlayButton> codeButtonMap = new HashMap<>();
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
        
        @Override public void recalculateSlopes()
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
    
    public static class AndroidTouchPoint extends QCollidable
    {
        public AndroidTouchPoint(float px, float py) { this.addPoint((int)px, (int)py); }
    }
    
    public HashSet<ButtonEvent.Code> getButtonsForTouch(float px, float py)
    {
        HashSet<ButtonEvent.Code> result = new HashSet<>();
        AndroidTouchPoint touch = new AndroidTouchPoint(px, py);
        for (ButtonEvent.Code code : this.codeButtonMap.keySet())
        {
            AndroidOverlayButton button = this.codeButtonMap.get(code);
            if (button == null) { continue; }
            if (button.collidesWith(touch))
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
            switch (action)
            {
                case MotionEvent.ACTION_POINTER_DOWN: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_DOWN: 
                    this.touchButtonsPress(id, codes);
                    consumed = true;
                    break;
                
                case MotionEvent.ACTION_POINTER_UP: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_UP:
                    this.touchButtonsRelease(id, codes);
                    consumed = true;
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
                    consumed = true;
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
}