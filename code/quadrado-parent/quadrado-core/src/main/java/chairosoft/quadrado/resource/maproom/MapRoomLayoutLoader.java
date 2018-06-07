package chairosoft.quadrado.resource.maproom;

import chairosoft.quadrado.resource.loading.ResourceLoader;
import chairosoft.quadrado.resource.loading.decoder.SimpleClassNameKeyDecoder;
import chairosoft.quadrado.resource.loading.resolver.TextLinesResolver;

import java.util.List;

public class MapRoomLayoutLoader
    extends ResourceLoader<Class<?>, List<String>>
    implements SimpleClassNameKeyDecoder, TextLinesResolver<Class<?>>
{
    ////// Constants //////
    public static final String RESOURCE_ROOT = "maproom";
    
    ////// Constructor //////
    public MapRoomLayoutLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
}
