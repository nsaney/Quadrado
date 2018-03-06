package chairosoft.quadrado.ui.event;

public interface ButtonDeviceProvider {
    
    ////// Instance Methods //////
    ButtonDevice.Info[] getAvailableButtonDeviceInfo();
    ButtonDevice getButtonDevice(ButtonDevice.Info info);
    
}
