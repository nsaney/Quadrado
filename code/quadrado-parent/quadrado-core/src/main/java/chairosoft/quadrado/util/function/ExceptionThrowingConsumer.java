package chairosoft.quadrado.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionThrowingConsumer<T, Ex extends Throwable> {
    
    ////// Instance Methods - Abstract //////
    void accept(T input) throws Ex;
    
    
    ////// Instance Methods - Default //////
    default void wrap(T input, Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper) {
        try { this.accept(input); }
        catch (Throwable ex) { throw runtimeExceptionWrapper.apply(ex); }
    }
    
    
    ////// Static Methods //////
    static <T, Ex extends Throwable> void wrap(
        ExceptionThrowingConsumer<T, Ex> methodReference,
        T input,
        Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper
    ) {
        methodReference.wrap(input, runtimeExceptionWrapper);
    }
    
}
