package chairosoft.quadrado.resource.box_style;

import chairosoft.quadrado.resource.loading.decoder.SimpleClassNameKeyDecoder;
import chairosoft.quadrado.resource.loading.resolver.DrawingImageResolver;
import chairosoft.quadrado.resource.loading.ResourceLoader;
import chairosoft.quadrado.ui.graphics.DrawingImage;

public class BoxStyleImageLoader
    extends ResourceLoader<Class<?>, DrawingImage>
    implements SimpleClassNameKeyDecoder, DrawingImageResolver<Class<?>>
{
    ////// Constants //////
    public static final String RESOURCE_ROOT = "box_style";
    
    ////// Constructor //////
    public BoxStyleImageLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
}
