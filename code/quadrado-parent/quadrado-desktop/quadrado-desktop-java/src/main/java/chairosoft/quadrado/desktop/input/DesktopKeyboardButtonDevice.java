package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.ButtonDevice;
import chairosoft.quadrado.ui.input.ButtonListener;

import java.io.IOException;

public class DesktopKeyboardButtonDevice implements ButtonDevice {
    
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
