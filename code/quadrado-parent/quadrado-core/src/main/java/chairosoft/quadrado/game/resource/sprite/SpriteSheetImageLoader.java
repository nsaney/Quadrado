package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.loading.ResourceLoader;
import chairosoft.quadrado.game.resource.loading.decoder.ExactNameResourceDecoder;
import chairosoft.quadrado.game.resource.loading.resolver.DrawingImageResolver;
import chairosoft.quadrado.ui.graphics.DrawingImage;


public class SpriteSheetImageLoader
    extends ResourceLoader<String, DrawingImage>
    implements ExactNameResourceDecoder, DrawingImageResolver<String>
{
    
    ////// Constants //////
    public static final boolean IS_INTERNAL = true;
    public static final String RESOURCE_ROOT = "img/sheets";
    public static final String EXTENSION = "png";
    
    ////// Constructor //////
    public SpriteSheetImageLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
    
}
