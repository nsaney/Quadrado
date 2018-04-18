package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;

import java.io.IOException;

public class DesktopHidButtonDevice extends ButtonDeviceAdapter {
    
    ////// Constants /////
    public static final InfoDataKey<HIDDeviceInfo> HID_DEVICE_INFO = new InfoDataKey<>(HIDDeviceInfo.class, "HID_DEVICE_INFO");
    
    
    ////// Instance Fields //////
    private final Object operationLock = new Object();
    private HIDDevice hidDevice = null;
    public HIDDevice getHidDevice() { return this.hidDevice; }
    
    ////// Constructor /////
    protected DesktopHidButtonDevice(Info _info) {
        super(_info);
    }
    
    
    ////// Instance Methods //////
    @Override
    public void open() throws IOException {
        synchronized (this.operationLock) {
            if (this.isOpen()) { return; }
            HIDDeviceInfo hidDeviceInfo = this.info.getDataEntry(HID_DEVICE_INFO);
            if (hidDeviceInfo == null) { return; }
            this.hidDevice = hidDeviceInfo.open();
            // TODO: listen/request button state???
        }
    }
    
    @Override
    public boolean isOpen() {
        synchronized (this.operationLock) {
            return this.hidDevice != null;
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.operationLock) {
            if (!this.isOpen()) { return; }
            this.hidDevice.close();
        }
    }
}
