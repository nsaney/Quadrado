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

import android.view.MotionEvent;
import android.view.View;

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