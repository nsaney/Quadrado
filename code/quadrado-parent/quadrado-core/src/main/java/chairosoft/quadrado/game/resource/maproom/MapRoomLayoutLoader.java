package chairosoft.quadrado.game.resource.maproom;

import chairosoft.quadrado.game.resource.loading.ResourceLoader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MapRoomLayoutLoader extends ResourceLoader<String, List<String>> {
    
    ////// Constants //////
    public static final boolean IS_INTERNAL = true;
    public static final String DIRECTORY = "map";
    public static final String EXTENSION = "txt";
    
    
    ////// Constructor //////
    public MapRoomLayoutLoader() {
        super(IS_INTERNAL, DIRECTORY, EXTENSION);
    }
    
    
    ////// Instance Methods //////
    @Override
    protected String getResourceName(String resourceKey) {
        return resourceKey;
    }
    
    @Override
    protected List<String> resolve(InputStream resourceStream) throws IOException {
        return IOUtils.readLines(resourceStream, StandardCharsets.UTF_8);
    }
    
}
