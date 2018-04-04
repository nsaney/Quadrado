package chairosoft.quadrado.ui.input.button;

public interface ButtonDeviceProvider<B extends ButtonDevice> {
    
    ////// Instance Methods //////
    Class<? extends B> getProvidedClass();
    ButtonDevice.Info[] getAvailableButtonDeviceInfo();
    B getButtonDevice(ButtonDevice.Info info);
    
}
