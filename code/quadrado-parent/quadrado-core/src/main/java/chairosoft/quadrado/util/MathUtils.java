package chairosoft.quadrado.util;

public final class MathUtils {
    
    ////// Constructor //////
    private MathUtils() { throw new UnsupportedOperationException(); }
    
    
    ////// Static Methods //////
    public static int getWrappedValue(int val, int bound) {
        if (bound < 0) { return -getWrappedValue(val, -bound); }
        if (bound == 0) { return 0; }
        while (val < 0) { val += bound; }
        while (val >= bound) { val -= bound; }
        return val;
    }
    
}
