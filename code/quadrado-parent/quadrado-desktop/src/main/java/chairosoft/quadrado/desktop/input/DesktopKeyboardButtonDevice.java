package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import chairosoft.quadrado.ui.input.button.ButtonEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class DesktopKeyboardButtonDevice extends ButtonDeviceAdapter implements  KeyEventDispatcher {
    
    ////// Singleton //////
    public static final DesktopKeyboardButtonDevice SINGLETON = new DesktopKeyboardButtonDevice();
    
    
    ////// Instance Fields //////
    private final Object stateLock = new Object();
    private volatile KeyboardFocusManager keyboardFocusManager = null;
    
    
    ////// Constructor //////
    private DesktopKeyboardButtonDevice() {
        super(new Info(
            "Keyboard",
            "The keyboard attached to this computer"
        ));
    }
    
    
    ////// Instance Methods //////
    @Override
    public void open() {
        synchronized (this.stateLock) {
            if (this.isOpen()) { return; }
            this.keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            if (this.keyboardFocusManager == null) {
                throw new NullPointerException("The returned keyboard focus manager was null.");
            }
            this.keyboardFocusManager.addKeyEventDispatcher(this);
        }
    }
    
    @Override
    public boolean isOpen() {
        return this.keyboardFocusManager != null;
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) { return; }
            this.keyboardFocusManager.removeKeyEventDispatcher(this);
            this.keyboardFocusManager = null;
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();
        ButtonEvent.Code code = getButtonForKey(keyCode);
        ButtonEvent be = new ButtonEvent(this.info, code);
        int type = e.getID();
        if (type == KeyEvent.KEY_PRESSED) {
            this.forEachButtonListener(bl -> bl.buttonPressed(be));
        }
        else if (type == KeyEvent.KEY_RELEASED) {
            this.forEachButtonListener(bl -> bl.buttonReleased(be));
        }
        return true;
    }
    
    
    // TODO: generalize this
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
}