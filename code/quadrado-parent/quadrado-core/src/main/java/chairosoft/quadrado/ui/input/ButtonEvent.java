/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * ButtonEvent.java
 * ButtonEvent class definition
 * 
 */

package chairosoft.quadrado.ui.input;


public class ButtonEvent
{
    public static enum Code
    {
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        SELECT,
        START,
        A,
        B,
        X,
        Y,
        L,
        R,
        DEBUG_0,
        DEBUG_1,
        DEBUG_2,
        DEBUG_3,
        DEBUG_4,
        DEBUG_5,
        DEBUG_6,
        DEBUG_7,
        DEBUG_8,
        DEBUG_9
    }
    
    public final ButtonSource source;
    public final Code code;
    public ButtonEvent(ButtonSource _source, Code _code)
    {
        this.source = _source;
        this.code = _code;
    }
}