package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.ButtonDevice;
import chairosoft.quadrado.ui.input.ButtonEvent;
import chairosoft.quadrado.ui.input.ButtonListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DesktopKeyboardButtonDevice implements ButtonDevice, KeyListener {
    
    ////// Singleton //////
    public static final DesktopKeyboardButtonDevice SINGLETON = new DesktopKeyboardButtonDevice();
    
    
    ////// Instance Fields //////
    public final Info info;
    private final Set<ButtonListener> buttonListeners = new CopyOnWriteArraySet<>();
    private final Object stateLock = new Object();
    private boolean isOpen = false;
    private Component sourceComponent = null;
    
    
    ////// Constructor //////
    private DesktopKeyboardButtonDevice() {
        this.info = new Info(
            0,
            "keyboard",
            "The keyboard attached to this computer",
            "[Unknown Vendor]",
            "[Unknown Version]"
        ) {};
    }
    
    
    ////// Instance Methods //////
    @Override
    public Info getButtonDeviceInfo() {
        return this.info;
    }
    
    @Override
    public void open() {
        synchronized (this.stateLock) {
            if (this.isOpen) { return; }
            Component _sourceComponent = null; // TODO: find this somehow
            if (_sourceComponent == null) {
                throw new IllegalStateException("Cannot attach key listener to null component.");
            }
            this.sourceComponent = _sourceComponent;
            this.sourceComponent.addKeyListener(this);
            this.isOpen = false;
        }
    }
    
    @Override
    public boolean isOpen() {
        synchronized (this.stateLock) {
            return this.isOpen;
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen) { return; }
            
            this.isOpen = false;
        }
    }
    
    @Override
    public void addButtonListener(ButtonListener listener) {
        this.buttonListeners.add(listener);
    }
    
    @Override
    public void removeButtonListener(ButtonListener listener) {
        this.buttonListeners.remove(listener);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        ButtonEvent.Code code = getButtonForKey(e.getKeyCode());
        ButtonEvent be = new ButtonEvent(this.info, code);
        this.buttonListeners.forEach(bl -> bl.buttonPressed(be));
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        ButtonEvent.Code code = getButtonForKey(e.getKeyCode());
        ButtonEvent be = new ButtonEvent(this.info, code);
        this.buttonListeners.forEach(bl -> bl.buttonReleased(be));
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
