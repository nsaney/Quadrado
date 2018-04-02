package chairosoft.quadrado.ui.input.button;

public interface ButtonDeviceProvider {
    
    ////// Instance Methods //////
    ButtonDevice.Info[] getAvailableButtonDeviceInfo();
    ButtonDevice getButtonDevice(ButtonDevice.Info info);
    
}
