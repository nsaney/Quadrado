package chairosoft.quadrado.ui.input;

public interface ButtonDeviceProvider {
    
    ////// Instance Methods //////
    ButtonDevice.Info[] getAvailableButtonDeviceInfo();
    ButtonDevice getButtonDevice(ButtonDevice.Info info);
    
}
