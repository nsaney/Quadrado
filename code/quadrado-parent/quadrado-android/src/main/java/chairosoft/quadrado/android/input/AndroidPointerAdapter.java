/* 
 * Nicholas Saney 
 * 
 * Created: June 12, 2015
 * 
 * AndroidPointerAdapter.java
 * AndroidPointerAdapter class definition
 * 
 */

package chairosoft.quadrado.android.input;

import chairosoft.quadrado.android.system.AndroidDoubleBufferedUI;
import chairosoft.quadrado.ui.input.pointer.PointerEvent;

import android.view.MotionEvent;
import android.view.View;

public class AndroidPointerAdapter implements View.OnTouchListener
{
    private final AndroidDoubleBufferedUI androidDbui;
    public AndroidPointerAdapter(AndroidDoubleBufferedUI _androidDbui)
    {
        this.androidDbui = _androidDbui;
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
            float x = e.getX(i) * this.androidDbui.getWidth() / this.androidDbui.getActivity().getWidthPixels();
            float y = e.getY(i) * this.androidDbui.getHeight() / this.androidDbui.getActivity().getHeightPixels();
            switch (action)
            {
                case MotionEvent.ACTION_POINTER_DOWN: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_DOWN: 
                    this.androidDbui.getPointerListener().pointerPressed(new PointerEvent(PointerEvent.PRESSED, x, y));
                    consumed = true;
                    break;
                
                case MotionEvent.ACTION_POINTER_UP: 
                    if (e.getActionIndex() != i) { break; }
                    // fallthrough
                case MotionEvent.ACTION_UP:
                    this.androidDbui.getPointerListener().pointerPressed(new PointerEvent(PointerEvent.RELEASED, x, y));
                    consumed = true;
                    break;
                
                case MotionEvent.ACTION_MOVE:
                    this.androidDbui.getPointerListener().pointerPressed(new PointerEvent(PointerEvent.MOVED, x, y));
                    consumed = true;
                    break;
                    
                default:
                    break;
            }
        }
        
        return consumed;
    }
}