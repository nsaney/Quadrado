package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import com.intel.bluetooth.BluetoothConsts;
import org.junit.Test;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class DesktopInputTest {
    
    @Test
    public void test_ButtonDevices() {
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
                    device.open();
                    System.out.printf("   - %15s: %s\n", "#toString()", device);
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
