#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h>
#include <ws2bth.h>
#include <bluetoothapis.h>
#include "../__common/chairosoft_common.h"

JNIEXPORT void JNICALL
Java_chairosoft_io_bluetooth_BluetoothDeviceProvider_testBluetooth(JNIEnv * env, jclass clazz) {
    throw_new_RuntimeException(env, "hci_inquiry");
}
