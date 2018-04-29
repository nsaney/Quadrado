package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;
import purejavahidapi.dataparser.Capability;
import purejavahidapi.dataparser.ParsedReportDataItem;

import java.io.IOException;
import java.util.*;

public class DesktopHidButtonDevice extends ButtonDeviceAdapter implements InputReportListener {
    
    ////// Constants /////
    public static final InfoDataKey<HidDeviceInfo> HID_DEVICE_INFO = new InfoDataKey<>(HidDeviceInfo.class, "HID_DEVICE_INFO");
    
    
    ////// Instance Fields //////
    private final Object operationLock = new Object();
    private HidDevice hidDevice = null;
    public HidDevice getHidDevice() { return this.hidDevice; }
    protected final DeviceState state;
    
    
    ////// Constructor /////
    protected DesktopHidButtonDevice(Info _info) {
        super(_info);
        HidDeviceInfo hidDeviceInfo = this.info.getDataEntry(HID_DEVICE_INFO);
        this.state = new DeviceState(hidDeviceInfo);
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
        ParsedReportDataItem[] parsedReportDataItems = this.hidDevice.parseReport(
            Capability.Type.INPUT,
            reportId,
            data,
            dataLength
        );
        System.out.printf("**** [%s] HID Parsed Input (%d) =", this.hidDevice.hashCode(), parsedReportDataItems.length);
        for (int i = 0; i < parsedReportDataItems.length; ++i) {
            ParsedReportDataItem item = parsedReportDataItems[i];
            System.out.printf(" [%d][%02x/", i, item.getCapability().getUsagePage());
            boolean isParsed = item.getIsParsed();
            if (!isParsed) {
                System.out.print(" not parsed]");
                continue;
            }
            short[] parsedButtons = item.getParsedButtons();
            if (parsedButtons != null) {
                Capability.ButtonRange buttonRange = (Capability.ButtonRange)item.getCapability();
                System.out.printf("%d:%d][", buttonRange.getUsageMin(), buttonRange.getUsageMax());
                for (short parsedButton : parsedButtons) {
                    System.out.printf(" %s", parsedButton);
                }
                System.out.print("]");
            }
            long[] parsedValues = item.getParsedValues();
            if (parsedValues != null) {
                Capability.Value valueCap = (Capability.Value)item.getCapability();
                long maxValue = valueCap.getLogicalMax();
                String maxValueString = String.format("%x", maxValue);
                int maxValueStringLength = maxValueString.length();
                String parsedValueFormat = String.format(" 0x%%0%dx", maxValueStringLength);
                System.out.printf("%02x][", valueCap.getUsage());
                for (long parsedValue : parsedValues) {
                    System.out.printf(parsedValueFormat, parsedValue);
                }
                System.out.print("]");
            }
        }
        System.out.println("");
        Map<DeviceStateKey, Long> updatedItemsByKey;
        synchronized (this.operationLock) {
            updatedItemsByKey = this.state.updateAndDiff(parsedReportDataItems);
        }
        System.out.printf("**** [%s] Updated Items (%d) = %s\n", this.hidDevice.hashCode(), updatedItemsByKey.size(), updatedItemsByKey);
    }
    
    ////// Static Inner Classes //////
    public static class DeviceStateKey implements Comparable<DeviceStateKey> {
        //// Constants ////
        public static final Comparator<DeviceStateKey> COMPARATOR = Comparator
            .comparing((DeviceStateKey item) -> item.reportId)
            .thenComparing(item -> item.usagePage)
            .thenComparing(item -> item.usage)
            .thenComparing(item -> item.index);
        
        //// Instance Fields ////
        public final byte reportId;
        public final short usagePage;
        public final short usage;
        public final int index;
        
        //// Constructor ////
        public DeviceStateKey(byte _reportId, short _usagePage, short _usage, int _index) {
            this.reportId = _reportId;
            this.usagePage = _usagePage;
            this.usage = _usage;
            this.index = _index;
        }
        
        //// Instance Methods ////
        @Override
        public String toString() {
            return String.format(
                "[%02x:%02x/%02x][%d]",
                this.reportId,
                this.usagePage,
                this.usage,
                this.index
            );
        }
        
        @Override
        public int compareTo(DeviceStateKey that) {
            return COMPARATOR.compare(this, that);
        }
        
        @Override
        public boolean equals(Object that) {
            return (that instanceof DeviceStateKey) && COMPARATOR.compare(this, (DeviceStateKey)that) == 0;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.reportId, this.usagePage, this.usage, this.index);
        }
    }
    
    public static class DeviceState {
        //// Instance Fields ////
        protected Map<DeviceStateKey, Long> state = new HashMap<>();
        
        //// Constructor ////
        public DeviceState(HidDeviceInfo deviceInfo) {
            if (deviceInfo == null) { return; }
            Capability[] capabilities = deviceInfo.getCapabilities();
            if (capabilities == null) { return; }
            for (Capability capability : capabilities) {
                if (capability == null) { continue; }
                byte reportId = capability.getReportId();
                short usagePage = capability.getUsagePage();
                if (capability instanceof Capability.ButtonRange) {
                    Capability.ButtonRange buttonRange = (Capability.ButtonRange)capability;
                    short usageMin = buttonRange.getUsageMin();
                    short usageMax = buttonRange.getUsageMax();
                    for (short usage = usageMin; usage <= usageMax; ++usage) {
                        DeviceStateKey key = new DeviceStateKey(reportId, usagePage, usage, 0);
                        this.state.put(key, null);
                    }
                }
                else if (capability instanceof Capability.Value) {
                    Capability.Value valueCap = (Capability.Value)capability;
                    short usage = valueCap.getUsage();
                    int reportCount = valueCap.getReportCount();
                    for (int index = 0; index < reportCount; ++index) {
                        DeviceStateKey key = new DeviceStateKey(reportId, usagePage, usage, index);
                        this.state.put(key, null);
                    }
                }
            }
        }
        
        //// Instance Methods ////
        public void clear() {
            this.state.clear();
        }
        
        public Map<DeviceStateKey, Long> updateAndDiff(ParsedReportDataItem[] parsedItems) {
            Map<DeviceStateKey, Long> diff = new HashMap<>();
            for (ParsedReportDataItem parsedItem : parsedItems) {
                Capability capability = parsedItem.getCapability();
                if (capability == null) { continue; }
                byte reportId = capability.getReportId();
                short usagePage = capability.getUsagePage();
                if (capability instanceof Capability.ButtonRange) {
                    short[] parsedButtons = parsedItem.getParsedButtons();
                    Capability.ButtonRange buttonRange = (Capability.ButtonRange)capability;
                    int buttonCount = buttonRange.getReportBitLength();
                    short usageMin = buttonRange.getUsageMin();
                    short usageMax = buttonRange.getUsageMax();
                    long[] updatedStates = new long[1 + buttonCount];
                    for (short usage : parsedButtons) {
                        updatedStates[usage - usageMin] = 1L;
                    }
                    for (short i = 0, usage = usageMin; usage <= usageMax; ++i, ++usage) {
                        long updatedValue = updatedStates[i];
                        this.doIndividualDiff(diff, reportId, usagePage, usage, 0, updatedValue);
                    }
                }
                else if (capability instanceof Capability.Value) {
                    long[] parsedValues = parsedItem.getParsedValues();
                    Capability.Value valueCap = (Capability.Value)capability;
                    short usage = valueCap.getUsage();
                    int reportCount = valueCap.getReportCount();
                    int maxIndex = reportCount < parsedValues.length ? reportCount : parsedValues.length;
                    for (int index = 0; index < maxIndex; ++index) {
                        long updatedValue = parsedValues[index];
                        this.doIndividualDiff(diff, reportId, usagePage, usage, index, updatedValue);
                    }
                }
            }
            return diff;
        }
        
        protected void doIndividualDiff(
            Map<DeviceStateKey, Long> diff,
            byte reportId,
            short usagePage,
            short usage,
            int index,
            Long updatedValue
        ) {
            DeviceStateKey key = new DeviceStateKey(reportId, usagePage, usage, index);
            Long currentValue = this.state.get(key);
            if (!Objects.equals(currentValue, updatedValue)) {
                this.state.put(key, updatedValue);
                diff.put(key, updatedValue);
            }
        }
    }
}
