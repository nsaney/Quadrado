package chairosoft.quadrado.desktop.event;

import org.junit.Test;

import static org.junit.Assert.*;

public class DesktopButtonDeviceTest {
    
    @Test
    public void test_desktopButtonDevice() throws Exception {
        System.out.println("java.library.path = ");
        System.out.println(System.getProperty("java.library.path", "[no path found]").replaceAll(":", "\n"));
        System.out.println("");
        
        System.loadLibrary("quadrado-desktop-2.0-alpha");
        DesktopButtonDevice buttonDevice = new DesktopButtonDevice();
        System.out.println("Button Device ID: " + buttonDevice.getId());
        buttonDevice.close();
    }
    
}