package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;

public class DesktopHidButtonDevice extends ButtonDeviceAdapter implements InputReportListener {
    
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
            this.hidDevice.setInputReportListener(this);
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
    
    @Override
    public void onInputReport(HidDevice source, byte reportId, byte[] data, int dataLength) {
        if (source != this.hidDevice) { return; }
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        int zeroCount = 0;
        for (int i = 0; i < dataLength; ++i) {
            byte b = data[i];
            if (b == 0) {
                ++zeroCount;
            }
            else {
                for (int z = 0; z < zeroCount; ++z) {
                    sb.append("00000000 ");
                }
                for (int s = 1 << 7; s > 0; s = s >> 1) {
                    sb.append((b & s) == 0 ? 0 : 1);
                }
                sb.append(" ");
                zeroCount = 0;
            }
        }
        if (zeroCount > 0) {
            sb.append(String.format("0... * %s ", zeroCount));
        }
        sb.append("]");
        System.out.printf("** HID Input Report 0x%02x from %s: %s\n", reportId, source.hashCode(), sb);
    }
}
