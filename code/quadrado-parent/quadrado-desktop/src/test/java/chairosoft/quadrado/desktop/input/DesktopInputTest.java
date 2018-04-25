package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

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
                System.out.printf("   - %15s: %s\n", "Desc", info.description)
            }
            System.out.println("---------------------------------");
        }
    }
    
}
