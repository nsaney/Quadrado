package chairosoft.quadrado.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionThrowingFunction<T, R, Ex extends Throwable> {
    
    ////// Instance Methods - Abstract //////
    R apply(T input) throws Ex;
    
    
    ////// Instance Methods - Default //////
    default R applyOrDefault(T input, R defaultResult) {
        try { return this.apply(input); }
        catch (Throwable ex) { return defaultResult; }
    }
    
    default R applyOrNull(T input) {
        return this.applyOrDefault(input, null);
    }
    
    default R applyOrWrap(T input, Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper) {
        try { return this.apply(input); }
        catch (Throwable ex) { throw runtimeExceptionWrapper.apply(ex); }
    }
    
    ////// Static Methods //////
    static <T, R, Ex extends Throwable> R applyOrDefault(
        ExceptionThrowingFunction<T, R, Ex> methodReference,
        T input,
        R defaultResult
    ) {
        return methodReference.applyOrDefault(input, defaultResult);
    }
    
    static <T, R, Ex extends Throwable> R applyOrNull(
        ExceptionThrowingFunction<T, R, Ex> methodReference,
        T input
    ) {
        return methodReference.applyOrNull(input);
    }
    
    static <T, R, Ex extends Throwable> R applyOrWrap(
        ExceptionThrowingFunction<T, R, Ex> methodReference,
        T input,
        Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper
    ) {
        return methodReference.applyOrWrap(input, runtimeExceptionWrapper);
    }
    
}
