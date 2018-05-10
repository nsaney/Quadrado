package chairosoft.quadrado.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionThrowingRunnable<Ex extends Throwable> {
    
    ////// Instance Methods - Abstract //////
    void run() throws Ex;
    
    
    ////// Instance Methods - Default //////
    default void wrap(Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper) {
        try { this.run(); }
        catch (Throwable ex) { throw runtimeExceptionWrapper.apply(ex); }
    }
    
    
    ////// Static Methods //////
    static <Ex extends Throwable> void wrap(
        ExceptionThrowingRunnable<Ex> methodReference,
        Function<Throwable, ? extends RuntimeException> runtimeExceptionWrapper
    ) {
        methodReference.wrap(runtimeExceptionWrapper);
    }
    
}
