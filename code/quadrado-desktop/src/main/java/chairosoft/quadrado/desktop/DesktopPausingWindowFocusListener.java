package chairosoft.quadrado.desktop;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class DesktopPausingWindowFocusListener implements WindowFocusListener {
    public final DesktopDoubleBufferedUI desktopDbui;
    
    public DesktopPausingWindowFocusListener(DesktopDoubleBufferedUI _desktopDbui) {
        this.desktopDbui = _desktopDbui;
    }
    
    @Override
    public void windowGainedFocus(WindowEvent e) {
        synchronized (this.desktopDbui.pauseLock) {
            this.desktopDbui.isPaused = false;
            this.desktopDbui.pauseLock.notifyAll();
        }
    }
    
    @Override
    public void windowLostFocus(WindowEvent e) {
        synchronized (this.desktopDbui.pauseLock) {
            this.desktopDbui.isPaused = true;
        }
    }
}
