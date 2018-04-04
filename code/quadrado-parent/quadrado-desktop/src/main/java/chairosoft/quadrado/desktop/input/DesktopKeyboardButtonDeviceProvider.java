package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;

import java.io.IOException;

/**
 * A button device provider for desktop environments,
 * which provides a singleton keyboard button device.
 */
public class DesktopKeyboardButtonDeviceProvider implements ButtonDeviceProvider<DesktopKeyboardButtonDevice> {
    
    @Override
    public Class<? extends DesktopKeyboardButtonDevice> getProvidedClass() {
        return DesktopKeyboardButtonDevice.class;
    }
    
    @Override
    public IOException getLastException() {
        return null;
    }
    
    @Override
    public ButtonDevice.Info[] getAvailableButtonDeviceInfo() {
        return new ButtonDevice.Info[] { DesktopKeyboardButtonDevice.SINGLETON.info };
    }
    
    @Override
    public DesktopKeyboardButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return info == DesktopKeyboardButtonDevice.SINGLETON.info
               ? DesktopKeyboardButtonDevice.SINGLETON
               : null;
    }
    
}
