package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import chairosoft.quadrado.ui.input.button.ButtonListener;
import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;
import com.intel.bluetooth.BluetoothConsts;
import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.*;
import javax.bluetooth.UUID;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DesktopBluetoothButtonDevice extends ButtonDeviceAdapter implements DiscoveryListener {
    
    ////// Constants //////
    private static final int ATTR_ID_SERVICE_NAME = 0x0100;
    private static final UUID[] UUIDS = {
        BluetoothConsts.L2CAP_PROTOCOL_UUID,
        BluetoothConsts.SERIAL_PORT_UUID
    };
    private static final int[] ATTR_IDS = { ATTR_ID_SERVICE_NAME };
    public static final InfoDataKey<DiscoveredDevice> DISCOVERED_DEVICE = new InfoDataKey<>(
        DiscoveredDevice.class,
        "DISCOVERED_DEVICE"
    );
    
    
    ////// Instance Fields //////
    private final Object operationLock = new Object();
    private volatile boolean isOpen = false;
    protected final Map<UUID,List<ServiceRecord>> serviceRecordsByUuid = new HashMap<>();
    protected final AtomicReference<IOException> lastException = new AtomicReference<>(null);
    protected final AtomicReference<UUID> currentUuid = new AtomicReference<>(null);
    
    
    ////// Constructor //////
    protected DesktopBluetoothButtonDevice(Info _info) {
        super(_info);
    }
    
    ////// Instance Methods - Button Device //////
    @Override
    public void open() {
        // TODO: move this back to the constructor
        synchronized (this.operationLock) {
            if (this.isOpen()) { return; }
            this.isOpen = true;
            DiscoveredDevice discoveredDevice = this.info.getDataEntry(DISCOVERED_DEVICE);
            RemoteDevice remoteDevice = discoveredDevice.remoteDevice;
            try {
                LocalDevice bluetoothProvider = LocalDevice.getLocalDevice();
                DiscoveryAgent discoveryAgent = bluetoothProvider.getDiscoveryAgent();
                this.serviceRecordsByUuid.clear();
                for (UUID serviceUuid : UUIDS) {
                    this.currentUuid.set(serviceUuid);
                    UUID[] uuidSet = { serviceUuid };
                    discoveryAgent.searchServices(ATTR_IDS, uuidSet, remoteDevice, this);
                    while (true) {
                        try {
                            this.operationLock.wait();
                            break;
                        }
                        catch (InterruptedException ex) {
                            // nothing here right now
                        }
                    }
                }
            }
            catch (IOException ex) {
                this.lastException.set(ex);
            }
            finally {
                this.currentUuid.set(null);
            }
        }
    }
    
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }
    
    @Override
    public void close() throws IOException {
        // nothing here right now
    }
    
    ////// Instance Methods - Discovery Listener //////
    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void inquiryCompleted(int discType) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void servicesDiscovered(int transId, ServiceRecord[] serviceRecords) {
        UUID currentUuidValue = this.currentUuid.get();
        List<ServiceRecord> servicesFound = this.serviceRecordsByUuid.computeIfAbsent(
            currentUuidValue,
            k -> new ArrayList<>()
        );
        Stream.of(serviceRecords)
            .filter(sr -> getUrlForService(sr) != null)
            .forEach(servicesFound::add)
        ;
    }
    
    @Override
    public void serviceSearchCompleted(int transId, int respCode) {
        synchronized (this.operationLock) {
            this.operationLock.notifyAll();
        }
    }
    
    
    ////// Instance Methods - Object //////
    @Override
    public String toString() {
        return String.format(
            "%s|exception=%s|services=%s",
            super.toString(),
            this.lastException.get() != null,
            this.serviceRecordsByUuid.entrySet().stream().map(
                e -> String.format(
                    "%s:%s",
                    getNameForUuid(e.getKey()),
                    e.getValue().stream()
                        .map(DesktopBluetoothButtonDevice::getUrlForService)
                        .map(str -> '"' + str + '"')
                        .collect(Collectors.joining("|", "[", "]"))
                )
            ).collect(Collectors.joining(" , ", "{", "}"))
        );
    }
    
    
    ////// Static Inner Classes //////
    public static class DiscoveredDevice {
        //// Instance Fields ////
        public final RemoteDevice remoteDevice;
        public final DeviceClass deviceClass;
        
        //// Constructor ////
        public DiscoveredDevice(RemoteDevice _remoteDevice, DeviceClass _deviceClass) {
            this.remoteDevice = _remoteDevice;
            this.deviceClass = _deviceClass;
        }
    }
    
    
    ////// Static Methods /////
    public static ButtonDevice.Info getInfoForRemoteDevice(DiscoveredDevice discoveredDevice) {
        RemoteDevice remoteDevice = discoveredDevice.remoteDevice;
        String blueCoveDeviceAddress = remoteDevice.getBluetoothAddress();
        long id = RemoteDeviceHelper.getAddress(blueCoveDeviceAddress);
        String name = ExceptionThrowingSupplier.getOrDefault(
            () -> remoteDevice.getFriendlyName(false),
            "[unknown]"
        );
        String description = "Bluetooth Device";
        ButtonDevice.Info info = new ButtonDevice.Info(id, name, description);
        info.putDataEntry(DISCOVERED_DEVICE, discoveredDevice);
        return info;
    }
    
    public static String getUrlForService(ServiceRecord serviceRecord) {
        return serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
    }
    
    public static String getNameForUuid(UUID uuid) {
        if (BluetoothConsts.L2CAP_PROTOCOL_UUID.equals(uuid)) {
            return "L2CAP_PROTOCOL_UUID";
        }
        else if (BluetoothConsts.OBEX_PROTOCOL_UUID.equals(uuid)) {
            return "OBEX_PROTOCOL_UUID";
        }
        else if (BluetoothConsts.OBEXFileTransferServiceClass_UUID.equals(uuid)) {
            return "OBEXFileTransferServiceClass_UUID";
        }
        else if (BluetoothConsts.RFCOMM_PROTOCOL_UUID.equals(uuid)) {
            return "RFCOMM_PROTOCOL_UUID";
        }
        else {
            return "UNKNOWN";
        }
    }
}
