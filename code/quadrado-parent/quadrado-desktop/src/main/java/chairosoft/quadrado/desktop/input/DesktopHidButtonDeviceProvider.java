package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;
import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesktopHidButtonDeviceProvider implements ButtonDeviceProvider<DesktopHidButtonDevice> {
    
    static {
        ClassPathLibraryLoader.loadNativeHIDLibrary();
    }
    
    ////// Constants //////
    public static final HIDManager HID_MANAGER = ExceptionThrowingSupplier.getOrWrap(HIDManager::getInstance, RuntimeException::new);
    public static final int HID_USAGE_PAGE_GENERIC_DESKTOP = 0x01;
    public static final int HID_USAGE_ID_GENERIC_DESKTOP_GAME_PAD = 0x05;
    
    
    ////// Instance Methods //////
    @Override
    public Class<? extends DesktopHidButtonDevice> getProvidedClass() {
        return DesktopHidButtonDevice.class;
    }
    
    @Override
    public IOException getLastException() {
        return null;
    }
    
    @Override
    public ButtonDevice.Info[] getAvailableButtonDeviceInfo() {
        HIDDeviceInfo[] hidDeviceInfos = ExceptionThrowingSupplier.getOrWrap(HID_MANAGER::listDevices, RuntimeException::new);
        List<ButtonDevice.Info> results = new ArrayList<>(hidDeviceInfos.length);
        for (HIDDeviceInfo hidDeviceInfo : hidDeviceInfos) {
            if (hidDeviceInfo == null) { continue; }
            int usagePage = hidDeviceInfo.getUsage_page();
            if (usagePage != HID_USAGE_PAGE_GENERIC_DESKTOP) { continue; }
            int usageId = hidDeviceInfo.getUsage();
            if (usageId != HID_USAGE_ID_GENERIC_DESKTOP_GAME_PAD) { continue; }
            String serialNumber = hidDeviceInfo.getSerial_number();
            String manufacturer = hidDeviceInfo.getManufacturer_string();
            String productName = hidDeviceInfo.getProduct_string();
            ButtonDevice.Info info = new ButtonDevice.Info(
                String.format( "%s%s", manufacturer == null ? "" : (manufacturer + " "), productName),
                hidDeviceInfo.toString()
            );
            info.putDataEntry(ButtonDevice.SERIAL_NUMBER, serialNumber);
            info.putDataEntry(DesktopHidButtonDevice.HID_DEVICE_INFO, hidDeviceInfo);
            results.add(info);
        }
        return results.toArray(new ButtonDevice.Info[0]);
    }
    
    @Override
    public DesktopHidButtonDevice getButtonDevice(ButtonDevice.Info info) {
        return new DesktopHidButtonDevice(info);
    }
}
