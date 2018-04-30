package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import chairosoft.quadrado.ui.input.button.ButtonEvent;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;
import purejavahidapi.dataparser.Capability;
import purejavahidapi.dataparser.ParsedReportDataItem;

import java.io.IOException;
import java.util.*;
import java.util.function.LongBinaryOperator;

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
        synchronized (this.operationLock) {
            Map<DeviceStateKey, DeviceStateValueDiff> updatedItemsByKey = this.state.updateAndGetDiffMap(parsedReportDataItems);
            //System.out.printf("**** [%s] Updated Items (%d) = %s\n", this.hidDevice.hashCode(), updatedItemsByKey.size(), updatedItemsByKey);
            Map<ButtonEvent.Type, Set<ButtonEvent.Code>> buttonUpdateMultiMap = new EnumMap<>(ButtonEvent.Type.class);
            buttonUpdateMultiMap.put(ButtonEvent.Type.PRESSED, new HashSet<>());
            buttonUpdateMultiMap.put(ButtonEvent.Type.RELEASED, new HashSet<>());
            for (DeviceStateKey key : updatedItemsByKey.keySet()) {
                DeviceStateValueDiff diff = updatedItemsByKey.get(key);
                this.addButtonsForUpdate(buttonUpdateMultiMap, key, diff);
            }
            for (ButtonEvent.Type type : buttonUpdateMultiMap.keySet()) {
                Set<ButtonEvent.Code> codes = buttonUpdateMultiMap.getOrDefault(type, Collections.emptySet());
                for (ButtonEvent.Code code : codes) {
                    ButtonEvent be = new ButtonEvent(this.info, code);
                    this.forEachButtonListener(bl -> bl.handleButtonEvent(type, be));
                }
            }
        }
    }
    
    
    // TODO: generalize this
    public void addButtonsForUpdate(
        Map<ButtonEvent.Type, Set<ButtonEvent.Code>> buttonUpdateMultiMap,
        DeviceStateKey key,
        DeviceStateValueDiff diff
    ) {
        HidDevice hidDevice = this.getHidDevice();
        if (hidDevice == null) { return; }
        HidDeviceInfo hidDeviceInfo = hidDevice.getHidDeviceInfo();
        if (hidDeviceInfo == null) { return; }
        short vendorId = hidDeviceInfo.getVendorId();
        short productId = hidDeviceInfo.getProductId();
        if (vendorId == 0x057e && productId == 0x2007) {
            // Nintendo; Joy-Con (R)
            if (key.usagePage == 0x09) {
                // Buttons
                ButtonEvent.Type type = diff.updatedValue == 1 ? ButtonEvent.Type.PRESSED : ButtonEvent.Type.RELEASED;
                ButtonEvent.Code code = null;
                switch (key.usage) {
                    case 0x01: code = ButtonEvent.Code.B; break;
                    case 0x02: code = ButtonEvent.Code.A; break;
                    case 0x03: code = ButtonEvent.Code.Y; break;
                    case 0x04: code = ButtonEvent.Code.X; break;
                    case 0x05: code = ButtonEvent.Code.L; break;
                    case 0x06: code = ButtonEvent.Code.R; break;
                    case 0x0a: code = ButtonEvent.Code.START; break;
                    case 0x0d: code = ButtonEvent.Code.SELECT; break;
                }
                if (code != null) {
                    buttonUpdateMultiMap.get(type).add(code);
                }
            }
            else if (key.usagePage == 0x01 && key.usage == 0x39) {
                // Hat Switch
                //     0
                //   7   1
                // 6  [8]  2
                //   5   3
                //     4
                LongBinaryOperator rotate = (val, off) -> val > 7 ? val : ((val + off) % 8);
                long previousValue = diff.previousValue == null ? 8 : diff.previousValue;
                long oldUp = rotate.applyAsLong(previousValue, 1);
                long newUp = rotate.applyAsLong(diff.updatedValue, 1);
                long oldLeft = rotate.applyAsLong(previousValue, 3);
                long newLeft = rotate.applyAsLong(diff.updatedValue, 3);
                long oldDown = rotate.applyAsLong(previousValue, 5);
                long newDown = rotate.applyAsLong(diff.updatedValue, 5);
                long oldRight = rotate.applyAsLong(previousValue, 7);
                long newRight = rotate.applyAsLong(diff.updatedValue, 7);
                if (oldUp < 3 && newUp > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.RELEASED).add(ButtonEvent.Code.UP);
                }
                if (newUp < 3 && oldUp > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.PRESSED).add(ButtonEvent.Code.UP);
                }
                if (oldLeft < 3 && newLeft > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.RELEASED).add(ButtonEvent.Code.LEFT);
                }
                if (newLeft < 3 && oldLeft > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.PRESSED).add(ButtonEvent.Code.LEFT);
                }
                if (oldDown < 3 && newDown > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.RELEASED).add(ButtonEvent.Code.DOWN);
                }
                if (newDown < 3 && oldDown > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.PRESSED).add(ButtonEvent.Code.DOWN);
                }
                if (oldRight < 3 && newRight > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.RELEASED).add(ButtonEvent.Code.RIGHT);
                }
                if (newRight < 3 && oldRight > 2) {
                    buttonUpdateMultiMap.get(ButtonEvent.Type.PRESSED).add(ButtonEvent.Code.RIGHT);
                }
            }
        }
        
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
    
    public static class DeviceStateValueDiff {
        //// Instance Fields ////
        public final Long previousValue;
        public final long updatedValue;
        
        //// Constructor ////
        public DeviceStateValueDiff(Long _previousValue, long _updatedValue) {
            this.previousValue = _previousValue;
            this.updatedValue = _updatedValue;
        }
        
        //// Instance Methods ////
        @Override
        public String toString() {
            return String.format("[%s->%s]", this.previousValue, this.updatedValue);
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
        
        public Map<DeviceStateKey, DeviceStateValueDiff> updateAndGetDiffMap(ParsedReportDataItem[] parsedItems) {
            Map<DeviceStateKey, DeviceStateValueDiff> diffMap = new TreeMap<>();
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
                        this.doIndividualDiff(diffMap, reportId, usagePage, usage, 0, updatedValue);
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
                        this.doIndividualDiff(diffMap, reportId, usagePage, usage, index, updatedValue);
                    }
                }
            }
            return diffMap;
        }
        
        protected void doIndividualDiff(
            Map<DeviceStateKey, DeviceStateValueDiff> diffMap,
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
                DeviceStateValueDiff diff = new DeviceStateValueDiff(currentValue, updatedValue);
                diffMap.put(key, diff);
            }
        }
    }
}
