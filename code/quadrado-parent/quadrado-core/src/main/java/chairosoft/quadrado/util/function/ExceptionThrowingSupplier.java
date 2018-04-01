package chairosoft.quadrado.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionThrowingSupplier<R, Ex extends Throwable> {
    
    ////// Instance Methods - Abstract //////
    R get() throws Ex;
    
    
    ////// Instance Methods - Default //////
    default R getOrDefault(R defaultResult) {
        try { return this.get(); }
        catch (Throwable ex) { return defaultResult; }
    }
    
    default R getOrNull() {
        return this.getOrDefault(null);
    }
    
    default R getOrWrap(Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper) {
        try { return this.get(); }
        catch (Throwable ex) { throw runtimeExceptionWrapper.apply(ex); }
    }
    
    ////// Static Methods //////
    static <R, Ex extends Throwable> R getOrDefault(
        ExceptionThrowingSupplier<R, Ex> methodReference,
        R defaultResult
    ) {
        return methodReference.getOrDefault(defaultResult);
    }
    
    static <R, Ex extends Throwable> R getOrNull(
        ExceptionThrowingSupplier<R, Ex> methodReference
    ) {
        return methodReference.getOrNull();
    }
    
    static <R, Ex extends Throwable> R getOrWrap(
        ExceptionThrowingSupplier<R, Ex> methodReference,
        Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper
    ) {
        return methodReference.getOrWrap(runtimeExceptionWrapper);
    }
    
}
