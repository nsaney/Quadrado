package chairosoft.quadrado.asset._resources.resolver;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface TextLinesResolver<K> extends ResourceValueResolver<K, List<String>> {
    
    ////// Constants //////
    String EXTENSION = "txt";
    
    
    ////// Instance Methods //////
    @Override
    default List<String> resolve(String resourceName, InputStream resourceStream) throws IOException {
        return IOUtils.readLines(resourceStream, StandardCharsets.UTF_8);
    }
}
