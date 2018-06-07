package chairosoft.quadrado.resource.sprite;

import chairosoft.quadrado.resource.loading.ResourceLoader;
import chairosoft.quadrado.resource.loading.decoder.ExactNameResourceDecoder;
import chairosoft.quadrado.resource.loading.resolver.DrawingImageResolver;
import chairosoft.quadrado.ui.graphics.DrawingImage;


public class SpriteSheetImageLoader
    extends ResourceLoader<String, DrawingImage>
    implements ExactNameResourceDecoder, DrawingImageResolver<String>
{
    ////// Constants //////
    public static final boolean IS_INTERNAL = true;
    public static final String RESOURCE_ROOT = "sheet";
    
    ////// Constructor //////
    public SpriteSheetImageLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
}
