package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import com.codeminders.hidapi.HIDDevice;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DesktopInputTest {
    
    @Test
    public void test_ButtonDevices() throws Exception {
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
                String serialNumber = info.getDataEntry(ButtonDevice.SERIAL_NUMBER);
                if (serialNumber != null) {
                    System.out.printf("   - %15s: %s\n", "Serial Number", serialNumber);
                }
                System.out.printf("   - %15s: %s\n", "Name", info.name);
                System.out.printf("   - %15s: %s\n", "Desc", info.description);
                System.out.println("   [Opening Device...]");
                try (ButtonDevice device = provider.getButtonDevice(info)) {
                    try { device.open(); }
                    finally { System.out.printf("   - %15s: %s\n", "#toString()", device); }
                    if (device instanceof DesktopHidButtonDevice && device.isOpen()) {
                        DesktopHidButtonDevice desktopHidButtonDevice = (DesktopHidButtonDevice)device;
                        HIDDevice hidDevice = desktopHidButtonDevice.getHidDevice();
                        byte[] data = new byte[0x400];
                        hidDevice.readTimeout(data, 1000);
                        List<Byte> dataBoxed = new ArrayList<>(data.length);
                        for (byte b : data) { dataBoxed.add(b); }
                        String dataString = dataBoxed.stream()
                            .map(b -> String.format("%02x", b))
                            .collect(Collectors.joining(" ", "[", "]"))
                        ;
                        System.out.printf("   - %15s: %s\n", "#read()", dataString);
                    }
                }
                catch (Exception ex) {
                    PrintStream stderr = System.err;
                    System.setErr(System.out);
                    System.err.println(" ! An error occurred while using a button device: " + ex);
                    //ex.printStackTrace();
                    System.setErr(stderr);
                }
                finally {
                    System.out.println("   [Closed Device]");
                }
            }
            System.out.println("---------------------------------");
        }
    }
    
}
