/* 
 * Nicholas Saney 
 * 
 * Created: June 12, 2015
 * 
 * AndroidPointerAdapter.java
 * AndroidPointerAdapter class definition
 * 
 */

package chairosoft.android;

import chairosoft.ui.event.PointerListener;
import chairosoft.ui.event.PointerEvent;
import chairosoft.ui.geom.FloatPoint2D;

// import chairosoft.android.graphics.AndroidDrawingContext;
// import chairosoft.android.graphics.AndroidFontLayout;

// import chairosoft.quadrado.QCollidable;

// import android.graphics.Canvas;
// import android.graphics.LinearGradient;
// import android.graphics.Paint;
// import android.graphics.Path;
// import android.graphics.Rect;
// import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;

public class AndroidPointerAdapter implements View.OnTouchListener
{
    private final PointerListener pointerListener;
    public AndroidPointerAdapter(PointerListener _pointerListener)
    {
        this.pointerListener = _pointerListener;
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
            switch (action)
            {
                case MotionEvent.ACTION_POINTER_DOWN: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_DOWN: 
                    this.pointerListener.pointerPressed(new PointerEvent(PointerEvent.PRESSED, x, y));
                    consumed = true;
                    break;
                
                case MotionEvent.ACTION_POINTER_UP: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_UP:
                    this.pointerListener.pointerPressed(new PointerEvent(PointerEvent.RELEASED, x, y));
                    consumed = true;
                    break;
                
                case MotionEvent.ACTION_MOVE:
                    this.pointerListener.pointerPressed(new PointerEvent(PointerEvent.MOVED, x, y));
                    consumed = true;
                    break;
                    
                default:
                    break;
            }
        }
        
        return consumed;
    }
}