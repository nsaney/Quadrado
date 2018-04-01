package chairosoft.quadrado.game.resource.loading;

import chairosoft.quadrado.util.function.ExceptionThrowingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class ModularResourceLoader<K, V> extends ResourceLoader<K, V> {
    
    ////// Instance Fields //////
    public final Function<K, String> keyDecoder;
    public final ExceptionThrowingFunction<InputStream, V, IOException> streamResolver;
    
    
    ////// Constructor //////
    public ModularResourceLoader(
        boolean _isInternal,
        String _directory,
        String _extension,
        Function<K, String> _keyDecoder,
        ExceptionThrowingFunction<InputStream, V, IOException> _streamResolver
    ) {
        super(_isInternal, _directory, _extension);
        this.keyDecoder = _keyDecoder;
        this.streamResolver = _streamResolver;
    }
    
    
    ////// Instance Methods //////
    @Override
    protected String getResourceName(K resourceKey) {
        return this.keyDecoder.apply(resourceKey);
    }
    
    @Override
    protected V resolve(InputStream resourceStream) throws IOException {
        return this.streamResolver.apply(resourceStream);
    }
    
}
