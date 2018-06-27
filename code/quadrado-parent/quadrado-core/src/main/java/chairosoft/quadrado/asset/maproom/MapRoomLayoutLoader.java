package chairosoft.quadrado.asset.maproom;

import chairosoft.quadrado.asset._resources.ResourceLoader;
import chairosoft.quadrado.asset._resources.decoder.SimpleClassNameKeyDecoder;
import chairosoft.quadrado.asset._resources.resolver.TextLinesResolver;

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
