package chairosoft.io.bluetooth;

import chairosoft.quadrado.desktop.system.NarSystem;
import org.junit.Test;

public class BluetoothDeviceProviderTest {
    
    @Test
    public void test_bluetoothDevice() {
        NarSystem.loadLibrary();
        BluetoothDeviceProvider.testBluetooth();
    }
    
}