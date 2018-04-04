package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class DesktopInputTest {
    
    @Test
    public void test_ButtonDevices() {
        List<? extends ButtonDeviceProvider> buttonDeviceProviders = UserInterfaceProvider.get().getButtonDeviceProviders();
        for (ButtonDeviceProvider<?> provider : buttonDeviceProviders) {
            System.out.println("---------------------------------");
            System.out.println("Found provider for object of type: " + provider.getProvidedClass());
            ButtonDevice.Info[] availableInfos = provider.getAvailableButtonDeviceInfo();
            IOException lastException = provider.getLastException();
            if (lastException != null) {
                System.out.println("Exception occurred: " + lastException);
            }
            System.out.println("Available devices: " + availableInfos.length);
            for (ButtonDevice.Info info : availableInfos) {
                System.out.println("-- Found device: ");
                System.out.printf( "     ID: %1$s (0x%1$012x)\n", info.id);
                System.out.println("   Name: " + info.name);
                System.out.println("   Desc: " + info.description);
            }
            System.out.println("---------------------------------");
        }
    }
    
}
