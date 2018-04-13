package chairosoft.quadrado.desktop.input.hid;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;

import java.io.IOException;

public class DesktopHidButtonDevice extends ButtonDeviceAdapter {
    
    protected DesktopHidButtonDevice(Info _info) {
        super(_info);
    }
    
    @Override
    public void open() throws IOException {
    
    }
    
    @Override
    public boolean isOpen() {
        return false;
    }
    
    @Override
    public void close() throws IOException {
    
    }
    
}
