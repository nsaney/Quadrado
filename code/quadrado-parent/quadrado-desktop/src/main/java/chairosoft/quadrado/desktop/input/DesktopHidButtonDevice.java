package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import org.hid4java.HidDevice;

import java.io.IOException;

public class DesktopHidButtonDevice extends ButtonDeviceAdapter {
    
    ////// Constants /////
    public static final InfoDataKey<HidDevice> HID_DEVICE = new InfoDataKey<>(HidDevice.class, "HID_DEVICE");
    
    
    ////// Instance Fields //////
    private final Object operationLock = new Object();
    
    
    ////// Constructor /////
    protected DesktopHidButtonDevice(Info _info) {
        super(_info);
    }
    
    
    ////// Instance Methods //////
    @Override
    public void open() throws IOException {
        synchronized (this.operationLock) {
            if (this.isOpen()) { return; }
            HidDevice hidDevice = this.info.getDataEntry(HID_DEVICE);
            if (hidDevice == null) { return; }
            hidDevice.open();
        }
    }
    
    @Override
    public boolean isOpen() {
        synchronized (this.operationLock) {
            HidDevice hidDevice = this.info.getDataEntry(HID_DEVICE);
            return hidDevice != null && hidDevice.isOpen();
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.operationLock) {
            if (!this.isOpen()) { return; }
            HidDevice hidDevice = this.info.getDataEntry(HID_DEVICE);
            hidDevice.close();
        }
    }
}
