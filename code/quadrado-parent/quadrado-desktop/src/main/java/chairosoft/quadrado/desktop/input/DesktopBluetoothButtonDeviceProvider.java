package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;

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
    private final ArrayList<DesktopBluetoothButtonDevice.DiscoveredDevice> discoveredDevices = new ArrayList<>();
    protected final AtomicReference<IOException> lastException = new AtomicReference<>(null);
    
    
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
        this.discoveredDevices.clear();
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
                this.discoveredDevices.clear();
            }
        }
        return this.discoveredDevices.stream()
            .map(DesktopBluetoothButtonDevice::getInfoForRemoteDevice)
            .toArray(ButtonDevice.Info[]::new)
        ;
    }
    
    @Override
    public DesktopBluetoothButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return new DesktopBluetoothButtonDevice(info);
    }
    
    
    ////// Instance Methods - Discovery Listener //////
    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        this.discoveredDevices.add(new DesktopBluetoothButtonDevice.DiscoveredDevice(
            remoteDevice,
            deviceClass
        ));
    }
    
    @Override
    public void inquiryCompleted(int discType) {
        synchronized (DISCOVERY_LOCK) {
            DISCOVERY_LOCK.notifyAll();
        }
    }
    
    @Override
    public void servicesDiscovered(int transId, ServiceRecord[] serviceRecords) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void serviceSearchCompleted(int transId, int respCode) {
        throw new UnsupportedOperationException();
    }
    
}
