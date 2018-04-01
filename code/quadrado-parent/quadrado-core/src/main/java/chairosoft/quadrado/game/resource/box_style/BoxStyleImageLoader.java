package chairosoft.quadrado.game.resource.box_style;

import chairosoft.quadrado.game.resource.loading.resolver.DrawingImageResolver;
import chairosoft.quadrado.game.resource.loading.decoder.FullyQualifiedKeyDecoder;
import chairosoft.quadrado.game.resource.loading.ResourceLoader;
import chairosoft.quadrado.ui.graphics.DrawingImage;

public class BoxStyleImageLoader
    extends ResourceLoader<Class<?>, DrawingImage>
    implements FullyQualifiedKeyDecoder, DrawingImageResolver<Class<?>>
{
    
    ////// Constants //////
    public static final String EXTENSION = "box.png";
    
    ////// Constructor //////
    public BoxStyleImageLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
    
}
