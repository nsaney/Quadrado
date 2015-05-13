/* 
 * Nicholas Saney 
 * 
 * Created: April 10, 2015
 * 
 * DirectByteArrayOutputStream.java
 * DirectByteArrayOutputStream class definition
 * 
 */

package chairosoft.util;

import java.io.*; 
import java.util.*;

/**
 * A byte array output stream whose inner array can be directly accessed.
 */
public class DirectByteArrayOutputStream extends ByteArrayOutputStream
{
    public DirectByteArrayOutputStream() { }
    public DirectByteArrayOutputStream(int size) { super(size); }
    public byte[] getBuffer() { return this.buf; }
}   