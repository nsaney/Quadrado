package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.resource.loading.ClassBasedResourceKeyDecoder;
import chairosoft.quadrado.game.resource.loading.DrawingImageLoader;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.util.function.ExceptionThrowingFunction;

import java.io.IOException;
import java.io.InputStream;

public class TilesetImageLoader extends DrawingImageLoader<Class<? extends Tileset>> {
    
    ////// Constants //////
    public static final boolean IS_INTERNAL = true;
    public static final String RESOURCE_ROOT = "_tilesets";
    public static final String EXTENSION = "png";
    public static final ClassBasedResourceKeyDecoder<Tileset> KEY_DECODER = new ClassBasedResourceKeyDecoder<>(EXTENSION);
    public static final ExceptionThrowingFunction<InputStream, DrawingImage, IOException> STREAM_RESOLVER = UserInterfaceProvider.get()::createDrawingImage;
    
    ////// Constructor //////
    public TilesetImageLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, KEY_DECODER, STREAM_RESOLVER);
    }
    
}
