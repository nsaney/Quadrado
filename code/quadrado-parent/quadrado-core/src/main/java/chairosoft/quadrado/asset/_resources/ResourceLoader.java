package chairosoft.quadrado.asset._resources;

import chairosoft.quadrado.asset._resources.decoder.ResourceKeyDecoder;
import chairosoft.quadrado.asset._resources.resolver.ResourceValueResolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceLoader<K, V> implements ResourceKeyDecoder<K>, ResourceValueResolver<K, V> {
    
    ////// Constants //////
    public static final char FILE_SEPARATOR_CHAR = '/';
    public static final String FILE_SEPARATOR = "" + FILE_SEPARATOR_CHAR;
    public static final String EXTENSION_SEPARATOR = ".";
    
    ////// Instance Fields //////
    public final boolean isInternal;
    public final String directory;
    public final String extension;
    
    
    ////// Constructor //////
    protected ResourceLoader(boolean _isInternal, String _directory, String _extension) {
        this.isInternal = _isInternal;
        this.directory = _directory;
        this.extension = _extension;
    }
    
    
    ////// Instance Methods - Concrete //////
    protected String getAbsoluteResourcePath(String resourceName) {
        String fullResourceName = extendFilename(resourceName, this.extension);
        return getAbsoluteResourcePath(this.isInternal, this.directory, fullResourceName);
    }
    
    protected InputStream getResourceAsStream(String absoluteResourcePath) throws IOException {
        return getResourceAsStream(this.isInternal, absoluteResourcePath);
    }
    
    @Override
    public V load(K resourceKey) throws IOException {
        String resourceName = this.getResourceName(resourceKey);
        String absoluteResourcePath = this.getAbsoluteResourcePath(resourceName);
        InputStream resourceStream = this.getResourceAsStream(absoluteResourcePath);
        return this.resolve(resourceName, resourceStream);
    }
    
    
    ////// Static Methods //////
    public static String getInternalPath(String path) {
        return (path.charAt(0) == FILE_SEPARATOR_CHAR) ? path : (FILE_SEPARATOR + path);
    }
    
    public static String extendFilename(String resourceName, String... extensions) {
        return resourceName + EXTENSION_SEPARATOR + String.join(EXTENSION_SEPARATOR, extensions);
    }
    
    public static String joinPathElements(String root, String... pathElements) {
        return root + FILE_SEPARATOR + String.join(FILE_SEPARATOR, pathElements);
    }
    
    public static String getAbsoluteResourcePath(boolean isInternal, String directory, String fullResourceName) {
        String absolutePath = joinPathElements(directory, fullResourceName);
        if (isInternal) { absolutePath = getInternalPath(absolutePath); }
        return absolutePath;
    }
    
    public static InputStream getResourceAsStream(boolean isInternal, String absoluteResourcePath) throws IOException {
        if (isInternal) {
            return ResourceLoader.class.getResourceAsStream(absoluteResourcePath);
        }
        else {
            return new FileInputStream(absoluteResourcePath);
        }
    }
    
}
