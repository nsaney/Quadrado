package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;
import java.util.*;

public class DesktopHidButtonDevice extends ButtonDeviceAdapter implements InputReportListener {
    
    ////// Constants /////
    public static final InfoDataKey<HidDeviceInfo> HID_DEVICE_INFO = new InfoDataKey<>(HidDeviceInfo.class, "HID_DEVICE_INFO");
    
    
    ////// Instance Fields //////
    private final Object operationLock = new Object();
    private HidDevice hidDevice = null;
    public HidDevice getHidDevice() { return this.hidDevice; }
    protected DeviceState state = new DeviceState();
    
    
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
            this.state.clear();
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
            this.hidDevice.setInputReportListener(null);
            this.hidDevice.close();
            this.state.clear();
        }
    }
    
    @Override
    public void onInputReport(HidDevice source, byte reportId, byte[] data, int dataLength) {
        if (source != this.hidDevice) {
            return;
        }
        if (data.length != dataLength) {
            data = Arrays.copyOf(data, dataLength);
        }
        DeviceStateUpdate update;
        synchronized (this.operationLock) {
            update = this.state.update(reportId, data);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        int zeroCount = 0;
        for (int i = 0; i < update.diffMask.length; ++i) {
            byte m = update.diffMask[i];
            byte b = update.currentData[i];
            if (m == 0) {
                ++zeroCount;
            }
            else {
                for (int z = 0; z < zeroCount; ++z) {
                    sb.append("-------- ");
                }
                for (int s = 1 << 7; s > 0; s = s >> 1) {
                    boolean maskBit = (m & s) != 0;
                    boolean dataBit = (b & s) != 0;
                    char c = maskBit ? (dataBit ? '1' : '0') : (dataBit ? 'i' : 'o');
                    sb.append(c);
                }
                sb.append(" ");
                zeroCount = 0;
            }
        }
        if (zeroCount > 0) {
            sb.append(String.format("--... * %s ", zeroCount));
        }
        sb.append("]");
        System.out.printf("** HID Input Report 0x%02x from %s: %s\n", reportId, source.hashCode(), sb);
    }
    
    ////// Static Inner Classes //////
    
    public static class DeviceStateUpdate {
        //// Instance Fields ////
        public final byte[] diffMask;
        public final byte[] currentData;
        
        //// Constructor ////
        public DeviceStateUpdate(byte[] _diffMask, byte[] _currentData) {
            this.diffMask = _diffMask;
            this.currentData = _currentData;
        }
    }
    
    public static class DeviceState {
        //// Instance Fields ////
        protected final Map<Byte, byte[]> dataByReportId = new HashMap<>();
        
        //// Instance Methods ////
        public void clear() {
            this.dataByReportId.clear();
        }
        
        public DeviceStateUpdate update(byte reportId, byte[] currentData) {
            byte[] oldData = this.dataByReportId.put(reportId, currentData);
            if (oldData == null) { oldData = new byte[0]; }
            int oldLength = oldData.length;
            int currentLength = currentData.length;
            boolean oldDataLonger = oldLength > currentLength;
            boolean currentDataLonger = currentLength > oldLength;
            int totalLength = currentLength;
            if (oldDataLonger) {
                totalLength = oldLength;
                currentData = Arrays.copyOf(currentData, totalLength);
            }
            if (currentDataLonger) {
                oldData = Arrays.copyOf(oldData, totalLength);
            }
            byte[] diffMask = new byte[totalLength];
            for (int i = 0; i < totalLength; ++i) {
                byte old = oldData[i];
                byte current = currentData[i];
                for (int mask = 1 << 7, b = 1; mask > 0; mask = mask >> 1, ++b) {
                    boolean oldBit = (old & mask) != 0;
                    boolean currentBit = (current & mask) != 0;
                    if (oldBit != currentBit) {
                        diffMask[i] = (byte)(diffMask[i] | mask);
                    }
                }
            }
            return new DeviceStateUpdate(diffMask, currentData);
        }
    }
}
