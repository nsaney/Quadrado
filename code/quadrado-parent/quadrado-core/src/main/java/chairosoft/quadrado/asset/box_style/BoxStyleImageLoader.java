package chairosoft.quadrado.asset.box_style;

import chairosoft.quadrado.asset._resources.decoder.SimpleClassNameKeyDecoder;
import chairosoft.quadrado.asset._resources.resolver.DrawingImageResolver;
import chairosoft.quadrado.asset._resources.ResourceLoader;
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
