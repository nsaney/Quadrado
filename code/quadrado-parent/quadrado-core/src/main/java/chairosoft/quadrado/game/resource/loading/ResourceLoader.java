package chairosoft.quadrado.game.resource.loading;

import chairosoft.quadrado.util.function.ExceptionThrowingFunction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceLoader<K, V> {
    
    ////// Constants //////
    public static final char FILE_SEPARATOR_CHAR = '/';
    public static final String FILE_SEPARATOR = "" + FILE_SEPARATOR_CHAR;
    
    ////// Instance Fields //////
    public final boolean isInternal;
    public final String directory;
    
    
    ////// Constructor //////
    protected ResourceLoader(boolean _isInternal, String _directory) {
        this.isInternal = _isInternal;
        this.directory = _directory;
    }
    
    
    ////// Instance Methods - Abstract //////
    protected abstract String getRelativeResourcePath(K resourceKey);
    protected abstract V resolve(InputStream resourceStream) throws IOException;
    
    
    ////// Instance Methods - Concrete //////
    protected String getAbsoluteResourcePath(String relativeResourcePath) {
        String absolutePath = this.directory + FILE_SEPARATOR + relativeResourcePath;
        if (this.isInternal) { absolutePath = getInternalPath(absolutePath); }
        return absolutePath;
    }
    
    protected InputStream getResourceAsStream(String absoluteResourcePath) throws IOException {
        if (this.isInternal) {
            return ResourceLoader.class.getResourceAsStream(absoluteResourcePath);
        }
        else {
            return new FileInputStream(absoluteResourcePath);
        }
    }
    
    public V load(K resourceKey) throws IOException {
        String relativeResourcePath = this.getRelativeResourcePath(resourceKey);
        String absoluteResourcePath = this.getAbsoluteResourcePath(relativeResourcePath);
        InputStream resourceStream = this.getResourceAsStream(absoluteResourcePath);
        return this.resolve(resourceStream);
    }
    
    public V wrappedLoad(K key) {
        return ExceptionThrowingFunction.applyOrWrap(this::load, key, RuntimeException::new);
    }
    
    public V loadOrNull(K key) {
        return ExceptionThrowingFunction.applyOrNull(this::load, key);
    }
    
    
    ////// Static Methods //////
    protected static String getInternalPath(String path) {
        return (path.charAt(0) == FILE_SEPARATOR_CHAR) ? path : (FILE_SEPARATOR + path);
    }
    
}
