package chairosoft.io.bluetooth;

import chairosoft.quadrado.desktop.system.NarSystem;
import org.junit.Test;

import java.io.PrintStream;
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
            PrintStream stderr = System.err;
            System.setErr(System.out);
            ex.printStackTrace();
            System.setErr(stderr);
        }
    }
    
}