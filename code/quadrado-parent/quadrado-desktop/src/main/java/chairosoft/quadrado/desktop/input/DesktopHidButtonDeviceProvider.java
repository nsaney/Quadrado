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
        List<HidDeviceInfo> hidDeviceInfos = PureJavaHidApi.enumerateDevices();
        List<ButtonDevice.Info> results = new ArrayList<>(hidDeviceInfos.size());
        for (HidDeviceInfo hidDeviceInfo : hidDeviceInfos) {
            if (hidDeviceInfo == null) { continue; }
            int usagePage = hidDeviceInfo.getUsagePage();
            if (usagePage != HID_USAGE_PAGE_GENERIC_DESKTOP) { continue; }
//            int usageId = hidDeviceInfo.getUsage();
//            if (usageId != HID_USAGE_ID_GENERIC_DESKTOP_GAME_PAD) { continue; }
            String serialNumber = hidDeviceInfo.getSerialNumberString();
            String manufacturer = hidDeviceInfo.getManufacturerString();
            String productName = hidDeviceInfo.getProductString();
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
    
    
    ////// Main Method //////
    public static void main(String[] args) throws Exception {
        DesktopHidButtonDeviceProvider provider = new DesktopHidButtonDeviceProvider();
        DesktopHidButtonDevice.Info[] infos = provider.getAvailableButtonDeviceInfo();
        List<DesktopHidButtonDevice> openDevices = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        for (DesktopHidButtonDevice.Info info : infos) {
            DesktopHidButtonDevice device = provider.getButtonDevice(info);
            while (true) {
                System.out.println();
                System.out.printf("Open device [%s]? y/n: ", device);
                String nextLine = scanner.nextLine();
                if (nextLine.startsWith("y")) {
                    try { device.open(); }
                    catch (Exception ex) { ex.printStackTrace(); }
                    break;
                }
                if (nextLine.startsWith("n")) {
                    break;
                }
            }
            if (device.isOpen()) {
                System.out.println("** HID Device Opened: " + device.getHidDevice().hashCode());
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
