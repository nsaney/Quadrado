package chairosoft.quadrado.desktop.input.hid.jnaerated;
import com.ochafik.lang.jnaerator.runtime.CharByReference;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
/**
 * JNA Wrapper for library <b>hidapi</b><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public interface HidapiLibrary extends Library {
	public static final String JNA_LIBRARY_NAME = "hidapi";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(HidapiLibrary.JNA_LIBRARY_NAME);
	public static final HidapiLibrary INSTANCE = (HidapiLibrary)Native.loadLibrary(HidapiLibrary.JNA_LIBRARY_NAME, HidapiLibrary.class);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_init()</code><br>
	 * <i>native declaration : hidapi/hidapi.h:95</i>
	 */
	int hid_init();
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_exit()</code><br>
	 * <i>native declaration : hidapi/hidapi.h:108</i>
	 */
	int hid_exit();
	/**
	 * Original signature : <code>hid_device_info* hid_enumerate(unsigned short, unsigned short)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:131</i>
	 */
	hid_device_info hid_enumerate(short vendor_id, short product_id);
	/**
	 * Original signature : <code>__attribute__((dllexport)) void hid_free_enumeration(hid_device_info*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:141</i>
	 */
	void hid_free_enumeration(hid_device_info devs);
	/**
	 * Original signature : <code>hid_device* hid_open(unsigned short, unsigned short, const wchar_t*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:159</i><br>
	 * @deprecated use the safer methods {@link #hid_open(short, short, com.sun.jna.WString)} and {@link #hid_open(short, short, com.ochafik.lang.jnaerator.runtime.CharByReference)} instead
	 */
	@Deprecated 
	PointerByReference hid_open(short vendor_id, short product_id, CharByReference serial_number);
	/**
	 * Original signature : <code>hid_device* hid_open(unsigned short, unsigned short, const wchar_t*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:159</i>
	 */
	PointerByReference hid_open(short vendor_id, short product_id, WString serial_number);
	/**
	 * Original signature : <code>hid_device* hid_open_path(const char*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:174</i><br>
	 * @deprecated use the safer methods {@link #hid_open_path(java.lang.String)} and {@link #hid_open_path(com.sun.jna.Pointer)} instead
	 */
	@Deprecated 
	PointerByReference hid_open_path(Pointer path);
	/**
	 * Original signature : <code>hid_device* hid_open_path(const char*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:174</i>
	 */
	PointerByReference hid_open_path(String path);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_write(hid_device*, const unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:202</i><br>
	 * @deprecated use the safer methods {@link #hid_write(com.sun.jna.ptr.PointerByReference, byte[], com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_write(com.sun.jna.ptr.PointerByReference, com.sun.jna.Pointer, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_write(Pointer device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_write(hid_device*, const unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:202</i>
	 */
	int hid_write(PointerByReference device, byte data[], NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_write(hid_device*, const unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:202</i>
	 */
	int hid_write(PointerByReference device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_read_timeout(hid_device*, unsigned char*, size_t, int)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:223</i><br>
	 * @deprecated use the safer methods {@link #hid_read_timeout(com.sun.jna.ptr.PointerByReference, java.nio.ByteBuffer, com.ochafik.lang.jnaerator.runtime.NativeSize, int)} and {@link #hid_read_timeout(com.sun.jna.ptr.PointerByReference, com.sun.jna.Pointer, com.ochafik.lang.jnaerator.runtime.NativeSize, int)} instead
	 */
	@Deprecated 
	int hid_read_timeout(Pointer dev, Pointer data, NativeSize length, int milliseconds);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_read_timeout(hid_device*, unsigned char*, size_t, int)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:223</i>
	 */
	int hid_read_timeout(PointerByReference dev, ByteBuffer data, NativeSize length, int milliseconds);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_read_timeout(hid_device*, unsigned char*, size_t, int)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:223</i>
	 */
	int hid_read_timeout(PointerByReference dev, Pointer data, NativeSize length, int milliseconds);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_read(hid_device*, unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:243</i><br>
	 * @deprecated use the safer methods {@link #hid_read(com.sun.jna.ptr.PointerByReference, java.nio.ByteBuffer, com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_read(com.sun.jna.ptr.PointerByReference, com.sun.jna.Pointer, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_read(Pointer device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_read(hid_device*, unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:243</i>
	 */
	int hid_read(PointerByReference device, ByteBuffer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_read(hid_device*, unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:243</i>
	 */
	int hid_read(PointerByReference device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_set_nonblocking(hid_device*, int)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:263</i><br>
	 * @deprecated use the safer method {@link #hid_set_nonblocking(com.sun.jna.ptr.PointerByReference, int)} instead
	 */
	@Deprecated 
	int hid_set_nonblocking(Pointer device, int nonblock);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_set_nonblocking(hid_device*, int)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:263</i>
	 */
	int hid_set_nonblocking(PointerByReference device, int nonblock);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_send_feature_report(hid_device*, const unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:291</i><br>
	 * @deprecated use the safer methods {@link #hid_send_feature_report(com.sun.jna.ptr.PointerByReference, byte[], com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_send_feature_report(com.sun.jna.ptr.PointerByReference, com.sun.jna.Pointer, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_send_feature_report(Pointer device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_send_feature_report(hid_device*, const unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:291</i>
	 */
	int hid_send_feature_report(PointerByReference device, byte data[], NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_send_feature_report(hid_device*, const unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:291</i>
	 */
	int hid_send_feature_report(PointerByReference device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_feature_report(hid_device*, unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:316</i><br>
	 * @deprecated use the safer methods {@link #hid_get_feature_report(com.sun.jna.ptr.PointerByReference, java.nio.ByteBuffer, com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_get_feature_report(com.sun.jna.ptr.PointerByReference, com.sun.jna.Pointer, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_get_feature_report(Pointer device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_feature_report(hid_device*, unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:316</i>
	 */
	int hid_get_feature_report(PointerByReference device, ByteBuffer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_feature_report(hid_device*, unsigned char*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:316</i>
	 */
	int hid_get_feature_report(PointerByReference device, Pointer data, NativeSize length);
	/**
	 * Original signature : <code>__attribute__((dllexport)) void hid_close(hid_device*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:323</i><br>
	 * @deprecated use the safer method {@link #hid_close(com.sun.jna.ptr.PointerByReference)} instead
	 */
	@Deprecated 
	void hid_close(Pointer device);
	/**
	 * Original signature : <code>__attribute__((dllexport)) void hid_close(hid_device*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:323</i>
	 */
	void hid_close(PointerByReference device);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_manufacturer_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:335</i><br>
	 * @deprecated use the safer methods {@link #hid_get_manufacturer_string(com.sun.jna.ptr.PointerByReference, java.nio.CharBuffer, com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_get_manufacturer_string(com.sun.jna.ptr.PointerByReference, com.ochafik.lang.jnaerator.runtime.CharByReference, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_get_manufacturer_string(Pointer device, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_manufacturer_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:335</i>
	 */
	int hid_get_manufacturer_string(PointerByReference device, CharBuffer string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_manufacturer_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:335</i>
	 */
	int hid_get_manufacturer_string(PointerByReference device, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_product_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:347</i><br>
	 * @deprecated use the safer methods {@link #hid_get_product_string(com.sun.jna.ptr.PointerByReference, java.nio.CharBuffer, com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_get_product_string(com.sun.jna.ptr.PointerByReference, com.ochafik.lang.jnaerator.runtime.CharByReference, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_get_product_string(Pointer device, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_product_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:347</i>
	 */
	int hid_get_product_string(PointerByReference device, CharBuffer string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_product_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:347</i>
	 */
	int hid_get_product_string(PointerByReference device, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_serial_number_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:359</i><br>
	 * @deprecated use the safer methods {@link #hid_get_serial_number_string(com.sun.jna.ptr.PointerByReference, java.nio.CharBuffer, com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_get_serial_number_string(com.sun.jna.ptr.PointerByReference, com.ochafik.lang.jnaerator.runtime.CharByReference, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_get_serial_number_string(Pointer device, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_serial_number_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:359</i>
	 */
	int hid_get_serial_number_string(PointerByReference device, CharBuffer string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_serial_number_string(hid_device*, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:359</i>
	 */
	int hid_get_serial_number_string(PointerByReference device, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_indexed_string(hid_device*, int, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:372</i><br>
	 * @deprecated use the safer methods {@link #hid_get_indexed_string(com.sun.jna.ptr.PointerByReference, int, java.nio.CharBuffer, com.ochafik.lang.jnaerator.runtime.NativeSize)} and {@link #hid_get_indexed_string(com.sun.jna.ptr.PointerByReference, int, com.ochafik.lang.jnaerator.runtime.CharByReference, com.ochafik.lang.jnaerator.runtime.NativeSize)} instead
	 */
	@Deprecated 
	int hid_get_indexed_string(Pointer device, int string_index, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_indexed_string(hid_device*, int, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:372</i>
	 */
	int hid_get_indexed_string(PointerByReference device, int string_index, CharBuffer string, NativeSize maxlen);
	/**
	 * Original signature : <code>__attribute__((dllexport)) int hid_get_indexed_string(hid_device*, int, wchar_t*, size_t)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:372</i>
	 */
	int hid_get_indexed_string(PointerByReference device, int string_index, CharByReference string, NativeSize maxlen);
	/**
	 * Original signature : <code>wchar_t* hid_error(hid_device*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:383</i><br>
	 * @deprecated use the safer method {@link #hid_error(com.sun.jna.ptr.PointerByReference)} instead
	 */
	@Deprecated 
	CharByReference hid_error(Pointer device);
	/**
	 * Original signature : <code>wchar_t* hid_error(hid_device*)</code><br>
	 * <i>native declaration : hidapi/hidapi.h:383</i>
	 */
	CharByReference hid_error(PointerByReference device);
	public static class hid_device extends PointerType {
		public hid_device(Pointer address) {
			super(address);
		}
		public hid_device() {
			super();
		}
	};
}
