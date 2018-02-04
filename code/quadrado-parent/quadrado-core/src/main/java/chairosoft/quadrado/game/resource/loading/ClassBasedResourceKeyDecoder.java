package chairosoft.quadrado.game.resource.loading;

import java.util.function.Function;
import java.util.regex.Pattern;

import static chairosoft.quadrado.game.resource.loading.ResourceLoader.FILE_SEPARATOR;

public final class ClassBasedResourceKeyDecoder<C> implements Function<Class<? extends C>, String> {
    
    ////// Constants //////
    public static final Pattern PATTERN_CLASS_SEPARATOR = Pattern.compile(Pattern.quote("."));
    public static final String EXTENSION_SEPARATOR = ".";
    
    
    ////// Instance Fields //////
    public final String extension;
    
    
    ////// Constructor //////
    public ClassBasedResourceKeyDecoder(String _extension) {
        this.extension = _extension;
    }
    
    
    ////// Instance Methods //////
    @Override
    public String apply(Class<? extends C> resourceClass) {
        String name = resourceClass.getName();
        return PATTERN_CLASS_SEPARATOR.matcher(name).replaceAll(FILE_SEPARATOR) + EXTENSION_SEPARATOR + this.extension;
    }
}
