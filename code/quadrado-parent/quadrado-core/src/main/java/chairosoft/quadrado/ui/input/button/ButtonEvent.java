/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * ButtonEvent.java
 * ButtonEvent class definition
 * 
 */
package chairosoft.quadrado.ui.input.button;

public class ButtonEvent {
    ////// Static Inner Classes //////
    public enum Type {
        PRESSED,
        RELEASED
    }
    
    public enum Code {
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
    
    ////// Instance Fields /////
    public final ButtonDevice.Info sourceInfo;
    public final Code code;
    
    ////// Constructor //////
    public ButtonEvent(ButtonDevice.Info _sourceInfo, Code _code) {
        this.sourceInfo = _sourceInfo;
        this.code = _code;
    }
}