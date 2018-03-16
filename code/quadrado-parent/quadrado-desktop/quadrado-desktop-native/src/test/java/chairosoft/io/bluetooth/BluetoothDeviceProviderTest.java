package chairosoft.io.bluetooth;

import chairosoft.quadrado.desktop.system.NarSystem;
import org.junit.Test;

import java.util.regex.Pattern;

public class BluetoothDeviceProviderTest {
    
    @Test
    public void test_bluetoothDevice() {
        System.out.printf(
            "java.library.path = \n%s\n",
            System.getProperty("java.library.path", "[no path found]")
                .replaceAll(Pattern.quote(System.getProperty("path.separator", ":")), "\n")
        );
        NarSystem.loadLibrary();
        try {
            BluetoothDeviceProvider.testBluetooth();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}