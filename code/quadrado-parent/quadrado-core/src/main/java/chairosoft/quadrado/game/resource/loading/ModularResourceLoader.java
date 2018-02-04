package chairosoft.quadrado.game.resource.loading;

import chairosoft.quadrado.util.function.ExceptionThrowingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ModularResourceLoader<K, V> extends ResourceLoader<K, V> {
    
    ////// Instance Fields //////
    public final Function<K, String> keyDecoder;
    public final ExceptionThrowingFunction<InputStream, V, IOException> streamResolver;
    
    
    ////// Constructor //////
    public ModularResourceLoader(
        boolean _isInternal,
        String _directory,
        Function<K, String> _keyDecoder,
        ExceptionThrowingFunction<InputStream, V, IOException> _streamResolver
    ) {
        super(_isInternal, _directory);
        this.keyDecoder = _keyDecoder;
        this.streamResolver = _streamResolver;
    }
    
    
    ////// Instance Methods //////
    @Override
    protected String getRelativeResourcePath(K resourceKey) {
        return this.keyDecoder.apply(resourceKey);
    }
    
    @Override
    protected V resolve(InputStream resourceStream) throws IOException {
        return this.streamResolver.apply(resourceStream);
    }
    
    public V wrappedLoad(K key) {
        return ExceptionThrowingFunction.applyOrWrap(this::load, key, RuntimeException::new);
    }
}
