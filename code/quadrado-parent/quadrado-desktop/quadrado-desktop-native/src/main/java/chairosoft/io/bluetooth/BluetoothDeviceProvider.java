package chairosoft.io.bluetooth;

public final class BluetoothDeviceProvider {
    
    ////// Constructor //////
    private BluetoothDeviceProvider() { throw new UnsupportedOperationException(); }
    
    
    ////// Static Methods //////
    public static native void testBluetooth();
    
}
