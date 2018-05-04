package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceAdapter;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;
import com.intel.bluetooth.BlueCoveConfigProperties;
import com.intel.bluetooth.BluetoothConsts;
import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.*;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DesktopBluetoothButtonDevice extends ButtonDeviceAdapter implements DiscoveryListener {
    
    ////// Static Initialization //////
    static {
        // see: http://rzm-hamy-ws2.scc.kit.edu/SW-Projects/JavaAPI4Wiimote/other/wiimote-whiteboard.html
        System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
        // see: http://wiki.uweschmidt.org/WiimoteWhiteboard/FAQ#bluetooth-stack
        System.setProperty(BlueCoveConfigProperties.PROPERTY_STACK, "widcomm");
        System.setProperty(BlueCoveConfigProperties.PROPERTY_STACK_FIRST, "widcomm");
    }
    
    ////// Constants //////
    private static final int ATTR_ID_SERVICE_NAME = 0x0100;
    private static final UUID[] UUIDS = { BluetoothConsts.L2CAP_PROTOCOL_UUID };
    private static final int[] ATTR_IDS = { ATTR_ID_SERVICE_NAME };
    public static final InfoDataKey<DiscoveredDevice> DISCOVERED_DEVICE = new InfoDataKey<>(
        DiscoveredDevice.class,
        "DISCOVERED_DEVICE"
    );
    
    
    ////// Instance Fields //////
    private final Object operationLock = new Object();
    protected final Map<UUID,List<ServiceRecord>> serviceRecordsByUuid = new HashMap<>();
    private IOException lastException = null;
    private UUID currentUuid = null;
    private volatile Connection connection = null;
    
    
    ////// Constructor //////
    protected DesktopBluetoothButtonDevice(Info _info) {
        super(_info);
        synchronized (this.operationLock) {
            this.lastException = null;
            DiscoveredDevice discoveredDevice = this.info.getDataEntry(DISCOVERED_DEVICE);
            RemoteDevice remoteDevice = discoveredDevice.remoteDevice;
            try {
                LocalDevice bluetoothProvider = LocalDevice.getLocalDevice();
                DiscoveryAgent discoveryAgent = bluetoothProvider.getDiscoveryAgent();
                this.serviceRecordsByUuid.clear();
                for (UUID serviceUuid : UUIDS) {
                    this.currentUuid = serviceUuid;
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
                this.lastException = ex;
            }
            finally {
                this.currentUuid = null;
            }
        }
    }
    
    ////// Instance Methods - Button Device //////
    @Override
    public void open() throws IOException {
        synchronized (this.operationLock) {
            if (this.isOpen()) { return; }
            ServiceRecord serviceRecord = this.getFirstServiceRecord(BluetoothConsts.L2CAP_PROTOCOL_UUID);
            if (serviceRecord == null) { return; }
            String url = getUrlForService(serviceRecord);
            this.connection = Connector.open(url);
            System.out.printf("Connection[%s] = %s\n", this.connection.getClass(), this.connection);
        }
    }
    
    protected ServiceRecord getFirstServiceRecord(UUID uuid) {
        ServiceRecord result = null;
        List<ServiceRecord> serviceRecords = this.serviceRecordsByUuid.get(uuid);
        if (serviceRecords != null) {
            for (ServiceRecord serviceRecord : serviceRecords) {
                if (serviceRecord != null) {
                    result = serviceRecord;
                    break;
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean isOpen() {
        return this.connection != null;
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.operationLock) {
            if (!this.isOpen()) { return; }
            this.connection.close();
            this.connection = null;
        }
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
        UUID currentUuidValue = this.currentUuid;
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
            this.lastException != null,
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
    
    
    ////// Main Method (Test) //////
    public static void main(String[] args) throws Exception {
        List<? extends ButtonDeviceProvider> buttonDeviceProviders = UserInterfaceProvider.get().getButtonDeviceProviders();
        for (ButtonDeviceProvider<?> provider : buttonDeviceProviders) {
            System.out.println("---------------------------------");
            System.out.println("Found provider for object of type: " + provider.getProvidedClass().getSimpleName());
            System.out.println("[Searching with Provider...]");
            ButtonDevice.Info[] availableInfos = provider.getAvailableButtonDeviceInfo();
            IOException lastException = provider.getLastException();
            if (lastException != null) {
                System.out.println("Exception occurred: " + lastException);
            }
            System.out.println("Available device count: " + availableInfos.length);
            for (ButtonDevice.Info info : availableInfos) {
                System.out.println("-- Found device: ");
                System.out.printf("   - %15s: %2$s (0x%2$012x)\n", "ID", info.id);
                System.out.printf("   - %15s: %s\n", "Name", info.name);
                System.out.printf("   - %15s: %s\n", "Desc", info.description);
                DesktopBluetoothButtonDevice.DiscoveredDevice discoveredDevice = info.getDataEntry(
                    DesktopBluetoothButtonDevice.DISCOVERED_DEVICE
                );
                if (discoveredDevice != null) {
                    RemoteDevice remoteDevice = discoveredDevice.remoteDevice;
                    DeviceClass deviceClass = discoveredDevice.deviceClass;
                    boolean isAuthenticated = remoteDevice.isAuthenticated();
                    System.out.printf("   - %15s: %s\n", "Authenticated", isAuthenticated);
                    boolean isEncrypted = remoteDevice.isEncrypted();
                    System.out.printf("   - %15s: %s\n", "Encrypted", isEncrypted);
                    boolean isTrusted = remoteDevice.isTrustedDevice();
                    System.out.printf("   - %15s: %s\n", "Trusted", isTrusted);
                    String deviceClassText = BluetoothConsts.toString(deviceClass);
                    System.out.printf("   - %15s: %s\n", "Class", deviceClassText);
                    if (!isTrusted) { continue; }
                }
                System.out.println("   [Opening Device...]");
                try (ButtonDevice device = provider.getButtonDevice(info)) {
                    System.out.printf("   - %15s: %s\n", "#toString()", device);
                    device.open();
                }
                catch (Exception ex) {
                    PrintStream stderr = System.err;
                    System.setErr(System.out);
                    System.err.println("   An error occurred while using a button device: " + ex);
                    ex.printStackTrace();
                    System.setErr(stderr);
                }
            }
            System.out.println("---------------------------------");
        }
    }
}
