/* 
 * Nicholas Saney 
 * 
 * Created: April 10, 2015
 * 
 * ManagedByteArray.java
 * ManagedByteArray class definition
 * 
 */

package chairosoft.util;

import java.io.*; 
import java.util.*;

/**    
 * Java doesn't like to free resources until is really, really has to,
 * but that means GC might run on a large array during gameplay instead 
 * of during loading - so, at the risk of directly causing enormous
 * memory leakage, I will manage some large arrays myself.
 */
public final class ManagedByteArray extends DirectByteArrayInputStream
{
    // Static fields
    
    public static final int MINIMUM_MANAGED_SIZE = 1024 * 1024;
    public static final int ARRAY_SIZE_INCREMENT = 1024 * 1024 * 10;
    private static final int INDEX_UNKNOWN = -1;
    private static final int INDEX_FOR_WIMPY_ARRAYS = -2;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final Vector<ManagedByteArray> arrayPool = new Vector<>();
    
    
    // Static methods
    
    public static ManagedByteArray obtain(int size)
    {
        // System.err.printf("ManagedByteArray.obtain(%s)\n", size);
        ManagedByteArray result = null;
        if (size < MINIMUM_MANAGED_SIZE)
        {
            result = new ManagedByteArray(INDEX_FOR_WIMPY_ARRAYS, size);
        }
        else
        {
            int index = INDEX_UNKNOWN;
            int minDifference = Integer.MAX_VALUE;
            for (int i = 0; i < arrayPool.size(); ++i)
            {
                ManagedByteArray array = arrayPool.get(i);
                int difference = array.buf.length - size;
                if (!array.isCheckedOut() && 0 <= difference && difference < minDifference)
                {
                    minDifference = difference;
                    index = i;
                }
            }
            if (index == INDEX_UNKNOWN)
            {
                index = arrayPool.size();
                int increments = size / ARRAY_SIZE_INCREMENT;
                if (size % ARRAY_SIZE_INCREMENT != 0) { ++increments; }
                result = new ManagedByteArray(index, increments * ARRAY_SIZE_INCREMENT);
                arrayPool.add(result);
            }
            else
            {
                result = arrayPool.get(index);
            }
        }
        // System.err.println("Checked out index #" + result.index);
        result.checkedOutFlag = true;
        return result;
    }
    
    public static void release(ManagedByteArray array)
    {
        // System.err.printf("ManagedByteArray.release(#%s)\n", array.index);
        if (array.index != INDEX_FOR_WIMPY_ARRAYS) 
        {
            ManagedByteArray replacement = new ManagedByteArray(array);
            arrayPool.set(replacement.index, replacement);
        }
        array.buf = EMPTY_BYTE_ARRAY;
        array.checkedOutFlag = false;
    }
    
    public static void arraycopy(ManagedByteArray src, int srcPos, byte[] dest, int destPos, int length)
    {
        System.arraycopy(src.buf, srcPos, dest, destPos, length);
    }
    
    public static void arraycopy(byte[] src, int srcPos, ManagedByteArray dest, int destPos, int length)
    {
        System.arraycopy(src, srcPos, dest.buf, destPos, length);
    }
    
    public static void arraycopy(ManagedByteArray src, int srcPos, ManagedByteArray dest, int destPos, int length)
    {
        System.arraycopy(src.buf, srcPos, dest.buf, destPos, length);
    }
    
    public static int read(InputStream stream, ManagedByteArray array)
        throws IOException
    {
        return stream.read(array.buf);
    }
    
    public static int read(InputStream stream, ManagedByteArray array, int offset, int length)
        throws IOException
    {
        return stream.read(array.buf, offset, length);
    }
    
    public static void write(OutputStream stream, ManagedByteArray array)
        throws IOException
    {
        stream.write(array.buf);
    }
    
    public static void write(OutputStream stream, ManagedByteArray array, int offset, int length)
        throws IOException
    {
        stream.write(array.buf, offset, length);
    }
    
    
    // Instance fields
    
    private final int index;
    private boolean checkedOutFlag = false;
    
    
    // Constructors
    
    private ManagedByteArray(int _index, int _size)
    {
        super(new byte[_size]);
        // System.err.printf("new ManagedByteArray(%s, %s)\n", _index, _size);
        this.index = _index;
    }
    
    private ManagedByteArray(ManagedByteArray array)
    {
        super(array.buf);
        // System.err.printf("new ManagedByteArray(#%s)\n", array.index);
        this.index = array.index;
    }
    
    
    // Instance methods
    
    @Override 
    public void close() 
        throws IOException
    {
        ManagedByteArray.release(this);
    }
    
    @Override
    public byte[] getBuffer() 
    {
        throw new UnsupportedOperationException("You do not want to directly access the buffer of a managed byte array.");
    }
    
    public boolean isCheckedOut() 
    {
        return this.checkedOutFlag;
    }
    
    public byte get(int index)
    {
        return this.buf[index];
    }
    
    public int size()
    {
        return this.buf.length;
    }
}