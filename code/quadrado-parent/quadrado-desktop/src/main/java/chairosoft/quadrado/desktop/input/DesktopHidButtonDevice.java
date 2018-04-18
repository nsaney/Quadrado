package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;

public class DesktopHidButtonDevice extends ButtonDeviceAdapter {
    
    ////// Constants /////
    public static final InfoDataKey<HidDeviceInfo> HID_DEVICE_INFO = new InfoDataKey<>(HidDeviceInfo.class, "HID_DEVICE_INFO");
    
    
    ////// Instance Fields //////
    private final Object operationLock = new Object();
    private HidDevice hidDevice = null;
    public HidDevice getHidDevice() { return this.hidDevice; }
    
    ////// Constructor /////
    protected DesktopHidButtonDevice(Info _info) {
        super(_info);
    }
    
    
    ////// Instance Methods //////
    @Override
    public void open() throws IOException {
        synchronized (this.operationLock) {
            if (this.isOpen()) { return; }
            HidDeviceInfo hidDeviceInfo = this.info.getDataEntry(HID_DEVICE_INFO);
            if (hidDeviceInfo == null) { return; }
            this.hidDevice = PureJavaHidApi.openDevice(hidDeviceInfo);
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
