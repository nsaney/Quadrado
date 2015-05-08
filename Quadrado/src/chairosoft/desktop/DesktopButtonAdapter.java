/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * DesktopButtonAdapter.java
 * DesktopButtonAdapter class definition
 * 
 */

package chairosoft.desktop;


import chairosoft.ui.event.ButtonListener;
import chairosoft.ui.event.ButtonSource;
import chairosoft.ui.event.ButtonEvent;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class DesktopButtonAdapter extends KeyAdapter
{
    private final ButtonListener buttonListener;
    public DesktopButtonAdapter(ButtonListener _buttonListener)
    {
        this.buttonListener = _buttonListener;
    }
    
    public static ButtonEvent.Code getButtonForKey(int keyCode)
    {
        ButtonEvent.Code result = ButtonEvent.Code.NONE;
        switch (keyCode)
        {
            // Direction
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_S:     result = ButtonEvent.Code.LEFT; break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_F:     result = ButtonEvent.Code.RIGHT; break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_E:     result = ButtonEvent.Code.UP; break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_D:     result = ButtonEvent.Code.DOWN; break;
            
            // Select
            case KeyEvent.VK_TAB: 
            case KeyEvent.VK_QUOTE: result = ButtonEvent.Code.SELECT; break;
            
            // Start
            case KeyEvent.VK_ENTER: 
            case KeyEvent.VK_SPACE: result = ButtonEvent.Code.START; break;
            
            // Action 
            case KeyEvent.VK_J:     result = ButtonEvent.Code.A; break;
            case KeyEvent.VK_M:     result = ButtonEvent.Code.B; break;
            case KeyEvent.VK_K:     result = ButtonEvent.Code.X; break;
            case KeyEvent.VK_COMMA: result = ButtonEvent.Code.Y; break;
            
            // Debug
            case KeyEvent.VK_0:     result = ButtonEvent.Code.DEBUG_0; break;
            case KeyEvent.VK_1:     result = ButtonEvent.Code.DEBUG_1; break;
            case KeyEvent.VK_2:     result = ButtonEvent.Code.DEBUG_2; break;
            case KeyEvent.VK_3:     result = ButtonEvent.Code.DEBUG_3; break;
            case KeyEvent.VK_4:     result = ButtonEvent.Code.DEBUG_4; break;
            case KeyEvent.VK_5:     result = ButtonEvent.Code.DEBUG_5; break;
            case KeyEvent.VK_6:     result = ButtonEvent.Code.DEBUG_6; break;
            case KeyEvent.VK_7:     result = ButtonEvent.Code.DEBUG_7; break;
            case KeyEvent.VK_8:     result = ButtonEvent.Code.DEBUG_8; break;
            case KeyEvent.VK_9:     result = ButtonEvent.Code.DEBUG_9; break;
        }
        return result;
    }
    
    @Override
    public void keyPressed(KeyEvent ke)
    {
        ButtonEvent.Code code = DesktopButtonAdapter.getButtonForKey(ke.getKeyCode());
        ButtonEvent be = new ButtonEvent(ButtonSource.KEYBOARD, code);
        this.buttonListener.buttonPressed(be);
    }
    
    @Override
    public void keyReleased(KeyEvent ke)
    {
        ButtonEvent.Code code = DesktopButtonAdapter.getButtonForKey(ke.getKeyCode());
        ButtonEvent be = new ButtonEvent(ButtonSource.KEYBOARD, code);
        this.buttonListener.buttonReleased(be);
    }
}