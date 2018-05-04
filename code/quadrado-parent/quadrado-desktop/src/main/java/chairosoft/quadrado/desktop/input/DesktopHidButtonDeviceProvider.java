package chairosoft.quadrado.desktop.input;

import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DesktopHidButtonDeviceProvider implements ButtonDeviceProvider<DesktopHidButtonDevice> {
    
    ////// Constants //////
    public static final short HID_USAGE_PAGE_GENERIC_DESKTOP = 0x01;
    public static final short HID_USAGE_ID_GENERIC_DESKTOP_GAME_PAD = 0x05;
    
    
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
        List<HidDeviceInfo> hidDeviceInfos = PureJavaHidApi.enumerateDevices();
        List<ButtonDevice.Info> results = new ArrayList<>(hidDeviceInfos.size());
        for (HidDeviceInfo hidDeviceInfo : hidDeviceInfos) {
            if (hidDeviceInfo == null) { continue; }
            short usagePage = hidDeviceInfo.getUsagePage();
            if (usagePage != HID_USAGE_PAGE_GENERIC_DESKTOP) { continue; }
            short usageId = hidDeviceInfo.getUsageId();
            if (usageId != HID_USAGE_ID_GENERIC_DESKTOP_GAME_PAD) { continue; }
            String serialNumber = hidDeviceInfo.getSerialNumberString();
            String manufacturer = hidDeviceInfo.getManufacturerString();
            String productName = hidDeviceInfo.getProductString();
            short vendorId = hidDeviceInfo.getVendorId();
            short productId = hidDeviceInfo.getProductId();
            short version = hidDeviceInfo.getReleaseNumber();
            String path = hidDeviceInfo.getPath();
            ButtonDevice.Info info = new ButtonDevice.Info(
                manufacturer == null ? productName : String.format("%s %s", manufacturer, productName),
                String.format("Vendor=0x%04x; Product=0x%04x; Version=0x%04d; Path=%s", vendorId, productId, version, path)
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
    
    
    ////// Main Method //////
    public static void main(String[] args) throws Exception {
        DesktopHidButtonDeviceProvider provider = new DesktopHidButtonDeviceProvider();
        DesktopHidButtonDevice.Info[] infos = provider.getAvailableButtonDeviceInfo();
        List<DesktopHidButtonDevice> openDevices = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        for (DesktopHidButtonDevice.Info info : infos) {
            DesktopHidButtonDevice device = provider.getButtonDevice(info);
            try { device.open(); }
            catch (Exception ex) { ex.printStackTrace(); }
            if (device.isOpen()) {
                System.out.printf(
                    "** HID Device Opened: %s [vendor=0x%04x, product=0x%04x]\n",
                    device.getHidDevice().hashCode(),
                    device.getHidDevice().getHidDeviceInfo().getVendorId(),
                    device.getHidDevice().getHidDeviceInfo().getProductId()
                );
                openDevices.add(device);
            }
        }
        while (true) {
            System.out.println();
            System.out.println("Enter 'q' to quit.");
            if (scanner.nextLine().trim().startsWith("q")) { break; }
        }
        for (DesktopHidButtonDevice device : openDevices) {
            try { device.close(); }
            catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
