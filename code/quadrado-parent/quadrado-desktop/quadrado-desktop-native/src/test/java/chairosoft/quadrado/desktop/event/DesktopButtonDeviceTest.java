package chairosoft.quadrado.desktop.event;

import chairosoft.quadrado.desktop.system.NarSystem;

import org.junit.Test;

public class DesktopButtonDeviceTest {
    
    @Test
    public void test_desktopButtonDevice() throws Exception {
        System.out.println("java.library.path = ");
        System.out.println(System.getProperty("java.library.path", "[no path found]").replaceAll(":", "\n"));
        System.out.println("");
        
        NarSystem.loadLibrary();
        DesktopButtonDevice buttonDevice = new DesktopButtonDevice();
        System.out.println("Button Device ID: " + buttonDevice.getId());
        buttonDevice.close();
    }
    
}