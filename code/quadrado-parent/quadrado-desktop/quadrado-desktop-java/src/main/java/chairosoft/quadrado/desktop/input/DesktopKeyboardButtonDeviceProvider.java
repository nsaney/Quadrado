package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;

/**
 * A button device provider for desktop environments,
 * which provides a singleton keyboard button device.
 */
public class DesktopKeyboardButtonDeviceProvider implements ButtonDeviceProvider {
    
    @Override
    public ButtonDevice.Info[] getAvailableButtonDeviceInfo() {
        return new ButtonDevice.Info[] { DesktopKeyboardButtonDevice.SINGLETON.info };
    }
    
    @Override
    public ButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return info == DesktopKeyboardButtonDevice.SINGLETON.info
               ? DesktopKeyboardButtonDevice.SINGLETON
               : null;
    }
    
}
