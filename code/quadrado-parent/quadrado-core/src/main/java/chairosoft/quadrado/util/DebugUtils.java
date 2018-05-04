package chairosoft.quadrado.util;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DebugUtils {
    
    ////// Constructor //////
    private DebugUtils() { throw new UnsupportedOperationException(); }
    
    ////// Static Methods //////
    public static <T> String formattedArray(T[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(boolean[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(byte[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(char[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(int[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(short[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(long[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(float[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    public static String formattedArray(double[] array, String format) { return formattedArray(array.length, format, i -> array[i]); }
    private static String formattedArray(int length, String format, Function<Integer, Object> arrayIndexFn) {
        return IntStream.range(0, length)
            .mapToObj(i -> String.format(format, arrayIndexFn.apply(i)))
            .collect(Collectors.joining(" ", "[", "]"));
    }
    
}
