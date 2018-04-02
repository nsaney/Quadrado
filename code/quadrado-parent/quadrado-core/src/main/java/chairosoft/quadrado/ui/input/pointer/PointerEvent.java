/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * PointerEvent.java
 * PointerEvent class definition
 * 
 */

package chairosoft.quadrado.ui.input.pointer;


public class PointerEvent
{
    public static final int PRESSED = 0;
    public static final int MOVED = 1;
    public static final int RELEASED = 2;
    
    public final int state;
    public final float x;
    public final float y;
    public PointerEvent(int _state, float _x, float _y)
    {
        this.state = _state;
        this.x = _x;
        this.y = _y;
    }
}