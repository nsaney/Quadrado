/* 
 * Nicholas Saney 
 * 
 * Created: April 10, 2015
 * Modified: April 10, 2015
 * 
 * ByteConverter.java
 * ByteConverter class definition
 * 
 */

package chairosoft.utils;

import java.io.*; 
import java.util.*;

/**
 * A utility for converting between bytes and integers.
 */
public class ByteConverter
{
    private final byte[] buffer;
    
    // little-endian = [0] or [0, 8] or [0, 8, 16] or [0, 8, 16, 24]
    // big-endian    = [0] or [8, 0] or [16, 8, 0] or [24, 16, 8, 0]
    private final int[] endianBitShift;
    
    // little-endian = [000000ff] or [000000ff, 0000ffff] or [000000ff, 0000ffff, 00ffffff] or [000000ff, 0000ffff, 00ffffff, ffffffff]
    // big-endian    = [000000ff] or [0000ffff, 000000ff] or [00ffffff, 0000ffff, 000000ff] or [ffffffff, 00ffffff, 0000ffff, 000000ff]
    private final int[] endianByteMask;
    
    // this is the integer with only the sign bit set
    public final int signBit;
    
    // this is the integer with all bits set that would indicate overflow
    public final int overflowMask;
    
    // the min and max values
    public final int minValue;
    public final int maxValue;
    
    public ByteConverter(int bytesPerInteger, boolean isBigEndian)
    {
        if (bytesPerInteger < 1 || 4 < bytesPerInteger)
        {
            String message = String.format("ByteConverter expects between 1 and 4 bytes per integer. Got %s instead.", bytesPerInteger);
            throw new ArrayIndexOutOfBoundsException(message);
        }
        
        this.buffer = new byte[bytesPerInteger];
        
        this.endianBitShift = new int[bytesPerInteger];
        this.endianByteMask = new int[bytesPerInteger];
        int _minValue = -128;
        int _maxValue = 128;
        for (int b = 0; b < bytesPerInteger; ++b)
        {
            int bytesToShift = isBigEndian 
                ? (bytesPerInteger - b - 1) 
                : b;
            endianBitShift[b] = 8 * bytesToShift;
            
            int maskByteShift = isBigEndian
                ? (4 - bytesPerInteger + b)
                : (3 - b);
            endianByteMask[b] = -1 >>> (8 * maskByteShift);
            if (b > 0)
            {
                _minValue *= 256;
                _maxValue *= 256;
            }
        }
        this.signBit = 2 << ((8 * bytesPerInteger) - 1);
        this.overflowMask = -1 << ((8 * bytesPerInteger) -1);
        this.minValue = _minValue;
        this.maxValue = _maxValue - 1;
    }
    
    public int readInteger(InputStream stream)
        throws IOException
    {
        stream.read(this.buffer);
        return this.readInteger(this.buffer, 0);
    }
    
    public int readInteger(byte[] bytes, int offset)
    {
        int value = 0;
        for (int b = 0; b < this.endianBitShift.length; ++b)
        {
            value = value | ((bytes[offset + b] << this.endianBitShift[b]) & this.endianByteMask[b]);
        }
        if ((value & this.overflowMask) != 0) { value = value - this.signBit; }
        return value;
    }
    
    public void writeInteger(int value, OutputStream stream)
        throws IOException
    {
        this.writeInteger(value, this.buffer, 0);
        stream.write(this.buffer);
    }
    
    public void writeInteger(int value, byte[] bytes, int offset)
    {
        for (int b = 0; b < this.endianBitShift.length; ++b)
        {
            bytes[offset + b] = (byte)((value >> this.endianBitShift[b]) & 0xff);
        }
    }
}