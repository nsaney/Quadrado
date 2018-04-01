package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.loading.ClassBasedResourceKeyDecoder;
import chairosoft.quadrado.game.resource.loading.DrawingImageLoader;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.util.function.ExceptionThrowingFunction;
import chairosoft.quadrado.util.function.Identity;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;


public class SpriteSheetImageLoader extends DrawingImageLoader<String> {
    
    ////// Constants //////
    public static final boolean IS_INTERNAL = true;
    public static final String RESOURCE_ROOT = "img/sheets";
    public static final String EXTENSION = "png";
    public static final ExceptionThrowingFunction<InputStream, DrawingImage, IOException> STREAM_RESOLVER = UserInterfaceProvider.get()::createDrawingImage;
    
    ////// Constructor //////
    public SpriteSheetImageLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION, Identity.STRING, STREAM_RESOLVER);
    }
    
}
