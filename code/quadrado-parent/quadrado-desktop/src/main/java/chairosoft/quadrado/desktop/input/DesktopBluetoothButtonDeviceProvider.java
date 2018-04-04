package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class DesktopBluetoothButtonDeviceProvider
    implements ButtonDeviceProvider<DesktopBluetoothButtonDevice>
    , DiscoveryListener
{
    
    ////// Constants //////
    private static final Object DISCOVERY_LOCK = new Object();
    
    
    ////// Instance Fields //////
    private final ArrayList<RemoteDevice> remoteDevices = new ArrayList<>();
    protected AtomicReference<IOException> lastException = new AtomicReference<>(null);
    
    
    ////// Instance Methods - Button Device Provider //////
    @Override
    public Class<? extends DesktopBluetoothButtonDevice> getProvidedClass() {
        return DesktopBluetoothButtonDevice.class;
    }
    
    @Override
    public IOException getLastException() {
        return this.lastException.get();
    }
    
    @Override
    public ButtonDevice.Info[] getAvailableButtonDeviceInfo() {
        this.remoteDevices.clear();
        synchronized (DISCOVERY_LOCK) {
            try {
                LocalDevice bluetoothProvider = LocalDevice.getLocalDevice();
                DiscoveryAgent discoveryAgent = bluetoothProvider.getDiscoveryAgent();
                boolean started = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
                while (started) {
                    try {
                        DISCOVERY_LOCK.wait();
                        break;
                    }
                    catch (InterruptedException ex) {
                        // nothing here right now
                    }
                }
            }
            catch (IOException ex) {
                this.lastException.set(ex);
                this.remoteDevices.clear();
            }
        }
        return this.remoteDevices.stream()
            .map(DesktopBluetoothButtonDeviceProvider::getInfoForRemoteDevice)
            .toArray(ButtonDevice.Info[]::new)
        ;
    }
    
    @Override
    public DesktopBluetoothButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return null;
    }
    
    
    ////// Instance Methods - Discovery Listener //////
    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        this.remoteDevices.add(remoteDevice);
    }
    
    @Override
    public void inquiryCompleted(int discType) {
        synchronized (DISCOVERY_LOCK) {
            DISCOVERY_LOCK.notifyAll();
        }
    }
    
    @Override
    public void servicesDiscovered(int transId, ServiceRecord[] serviceRecords) {
        // nothing here right now
    }
    
    @Override
    public void serviceSearchCompleted(int transId, int respCode) {
        // nothing here right now
    }
    
    
    ////// Static Methods - Info Handling /////
    public static ButtonDevice.Info getInfoForRemoteDevice(RemoteDevice remoteDevice) {
        String blueCoveDeviceAddress = remoteDevice.getBluetoothAddress();
        long id = getLongValueFromBlueCoveDeviceAddress(blueCoveDeviceAddress);
        String name = ExceptionThrowingSupplier.getOrDefault(
            () -> remoteDevice.getFriendlyName(false),
            "[unknown]"
        );
        String description = "Bluetooth Device";
        return new ButtonDevice.Info(id, name, description);
    }
    
    public static long getLongValueFromBlueCoveDeviceAddress(String blueCoveDeviceAddress) {
        return Long.parseLong(blueCoveDeviceAddress, 16);
    }
    
    public static String getBlueCoveDeviceAddressFromLong(long longValue) {
        return String.format("%12X", longValue);
    }
    
}
