package chairosoft.quadrado.asset_builder;

import chairosoft.quadrado.asset._resources.ResourceLoader;
import chairosoft.quadrado.asset._resources.decoder.ExactNameResourceDecoder;
import chairosoft.quadrado.asset._resources.resolver.ResourceValueResolver;
import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;
import javafx.scene.image.Image;

import java.io.InputStream;

public class AppIconLoader
    extends ResourceLoader<String, Image>
    implements ExactNameResourceDecoder, ResourceValueResolver<String, Image>
{
    ////// Constants ///////
    public static final boolean IS_INTERNAL = true;
    public static final String RESOURCE_ROOT = "icons";
    public static final String EXTENSION = "png";
    
    
    ////// Constructor //////
    public AppIconLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
    
    
    ////// Instance Methods //////
    @Override
    public Image resolve(String resourceName, InputStream resourceStream) {
        return new Image(resourceStream);
    }
}
