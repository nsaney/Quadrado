package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.loading.ResourceLoader;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class SpriteSheetImageLoader extends ResourceLoader<Class<? extends QSprite<?, ?, ?>>, DrawingImage> {
    
    ////// Constants //////
    public static final String SPRITE_SHEET_IMAGE_RESOURCE_ROOT = "_sprites";
    public static final Pattern PATTERN_CLASS_SEPARATOR = Pattern.compile(Pattern.quote("."));
    public static final String SPRITE_SHEET_IMAGE_EXTENSION = ".png";
    
    
    ////// Constructor //////
    public SpriteSheetImageLoader() {
        super(true, SPRITE_SHEET_IMAGE_RESOURCE_ROOT);
    }
    
    
    ////// Instance Methods //////
    @Override
    protected String getRelativeResourcePath(Class<? extends QSprite<?, ?, ?>> spriteClass) {
        String name = spriteClass.getName();
        return PATTERN_CLASS_SEPARATOR.matcher(name).replaceAll(FILE_SEPARATOR) + SPRITE_SHEET_IMAGE_EXTENSION;
    }
    
    @Override
    protected DrawingImage resolve(InputStream resourceStream) throws IOException {
        return UserInterfaceProvider.get().createDrawingImage(resourceStream);
    }
}
