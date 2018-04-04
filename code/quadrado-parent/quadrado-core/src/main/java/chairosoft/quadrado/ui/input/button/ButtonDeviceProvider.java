package chairosoft.quadrado.ui.input.button;

import java.io.IOException;

public interface ButtonDeviceProvider<B extends ButtonDevice> {
    
    ////// Instance Methods //////
    Class<? extends B> getProvidedClass();
    IOException getLastException();
    ButtonDevice.Info[] getAvailableButtonDeviceInfo();
    B getButtonDevice(ButtonDevice.Info info);
    
}
