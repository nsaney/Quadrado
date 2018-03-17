package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.ButtonDevice;
import chairosoft.quadrado.ui.input.ButtonDeviceProvider;

public class DesktopKeyboardButtonDeviceProvider implements ButtonDeviceProvider {
    
    @Override
    public ButtonDevice.Info[] getAvailableButtonDeviceInfo() {
        return new ButtonDevice.Info[0];
    }
    
    @Override
    public ButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return null;
    }
    
}
