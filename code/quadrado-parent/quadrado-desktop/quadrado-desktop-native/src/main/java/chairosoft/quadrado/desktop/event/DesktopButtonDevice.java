package chairosoft.quadrado.desktop.event;

import chairosoft.quadrado.ui.event.ButtonDevice;
import chairosoft.quadrado.ui.event.ButtonListener;

import java.io.IOException;

public class DesktopButtonDevice implements ButtonDevice {
    
    ////// Instance Methods //////
    @Override
    public Info getButtonDeviceInfo() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void open() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addButtonListener(ButtonListener listener) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void removeButtonListener(ButtonListener listener) {
        throw new UnsupportedOperationException();
    }
    
}
