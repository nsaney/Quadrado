package chairosoft.quadrado.game.resource.maproom;

import chairosoft.quadrado.game.resource.loading.ResourceLoader;
import chairosoft.quadrado.game.resource.loading.resolver.TextLinesResolver;
import chairosoft.quadrado.game.resource.loading.decoder.FullyQualifiedKeyDecoder;

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
