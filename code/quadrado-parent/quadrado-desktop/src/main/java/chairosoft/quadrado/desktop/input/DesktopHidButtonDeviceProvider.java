package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesktopHidButtonDeviceProvider implements ButtonDeviceProvider<DesktopHidButtonDevice> {
    
    ////// Constants //////
    private static final HidServices HID_SERVICES = HidManager.getHidServices();
    
    
    ////// Instance Methods //////
    @Override
    public Class<? extends DesktopHidButtonDevice> getProvidedClass() {
        return DesktopHidButtonDevice.class;
    }
    
    @Override
    public IOException getLastException() {
        return null;
    }
    
    @Override
    public ButtonDevice.Info[] getAvailableButtonDeviceInfo() {
        HID_SERVICES.scan();
        List<HidDevice> hidDevices = HID_SERVICES.getAttachedHidDevices();
        List<ButtonDevice.Info> results = new ArrayList<>(hidDevices.size());
        for (HidDevice hidDevice : hidDevices) {
            if (hidDevice == null) { continue; }
            String serialNumber = hidDevice.getSerialNumber();
            if (serialNumber == null) { continue; }
            ButtonDevice.Info info = new ButtonDevice.Info(
                String.format(
                    "%s %s",
                    hidDevice.getManufacturer(),
                    hidDevice.getProduct()
                ),
                hidDevice.toString()
            );
            info.putDataEntry(ButtonDevice.SERIAL_NUMBER, serialNumber);
            info.putDataEntry(DesktopHidButtonDevice.HID_DEVICE, hidDevice);
            results.add(info);
        }
        return results.toArray(new ButtonDevice.Info[0]);
    }
    
    @Override
    public DesktopHidButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return new DesktopHidButtonDevice(info);
    }
}
