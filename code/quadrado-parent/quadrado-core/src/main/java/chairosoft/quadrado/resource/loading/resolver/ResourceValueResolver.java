package chairosoft.quadrado.resource.loading.resolver;

import chairosoft.quadrado.util.function.ExceptionThrowingFunction;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceValueResolver<K, V> {
    
    ////// Instance Methods - Abstract //////
    V resolve(String resourceName, InputStream resourceStream) throws IOException;
    V load(K resourceKey) throws IOException;
    
    
    ////// Instance Methods - Concrete //////
    default V wrappedLoad(K key) {
        return ExceptionThrowingFunction.applyOrWrap(this::load, key, RuntimeException::new);
    }
    
    default V loadOrNull(K key) {
        return ExceptionThrowingFunction.applyOrNull(this::load, key);
    }
    
}
