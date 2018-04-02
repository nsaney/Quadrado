package chairosoft.quadrado.resource.maproom;

import chairosoft.quadrado.resource.loading.ResourceLoader;
import chairosoft.quadrado.resource.loading.resolver.TextLinesResolver;
import chairosoft.quadrado.resource.loading.decoder.FullyQualifiedKeyDecoder;

import java.util.List;

public class MapRoomLayoutLoader
    extends ResourceLoader<Class<?>, List<String>>
    implements FullyQualifiedKeyDecoder, TextLinesResolver<Class<?>>
{
    
    ////// Constants //////
    public static final String EXTENSION = "map.txt";
    
    
    ////// Constructor //////
    public MapRoomLayoutLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
    
}
