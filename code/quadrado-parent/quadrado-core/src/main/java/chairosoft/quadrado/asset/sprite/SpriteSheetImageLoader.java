package chairosoft.quadrado.asset.sprite;

import chairosoft.quadrado.asset._resources.ResourceLoader;
import chairosoft.quadrado.asset._resources.decoder.ExactNameResourceDecoder;
import chairosoft.quadrado.asset._resources.resolver.DrawingImageResolver;
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
