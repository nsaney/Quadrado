package chairosoft.quadrado.desktop.event;

import chairosoft.quadrado.ui.event.ButtonDevice;

import java.io.IOException;

public class DesktopButtonDevice implements ButtonDevice {
    
    @Override
    public native long getId();
    
    @Override
    public native void close() throws IOException;
    
}
