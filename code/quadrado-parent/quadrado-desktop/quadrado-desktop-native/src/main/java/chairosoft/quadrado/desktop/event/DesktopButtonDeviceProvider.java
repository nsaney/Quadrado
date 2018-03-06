package chairosoft.quadrado.desktop.event;

import chairosoft.quadrado.ui.event.ButtonDevice;
import chairosoft.quadrado.ui.event.ButtonDeviceProvider;

public class DesktopButtonDeviceProvider implements ButtonDeviceProvider {
    
    @Override
    public ButtonDevice.Info[] getAvailableButtonDeviceInfo() {
        return new ButtonDevice.Info[0];
    }
    
    @Override
    public ButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return null;
    }
    
}
