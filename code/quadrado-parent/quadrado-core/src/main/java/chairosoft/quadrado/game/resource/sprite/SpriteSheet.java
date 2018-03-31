package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.loading.SoftMap;
import chairosoft.quadrado.ui.graphics.DrawingImage;

public class SpriteSheet {
    
    ////// Constants //////
    public static final SpriteSheetImageLoader IMAGE_LOADER = new SpriteSheetImageLoader();
    protected static final SoftMap<SpriteSheetConfig, SpriteSheet> SPRITE_SHEETS_BY_CONFIG = new SoftMap<>(SpriteSheet::new);
    
    
    ////// Instance Properties //////
    protected final DrawingImage[] imageArray;
    public DrawingImage getImage(int imageIndex) {
        return this.imageArray[imageIndex];
    }
    
    
    ////// Constructor //////
    protected SpriteSheet(SpriteSheetConfig config) {
        this.imageArray = IMAGE_LOADER.loadTiledImages(
            config.resourceClass,
            config.transparencyRgb,
            config.spriteWidth,
            config.spriteHeight
        );
    }
    
    
    ////// Static Methods - Soft Map //////
    public static SpriteSheet get(SpriteSheetConfig config) {
        return SPRITE_SHEETS_BY_CONFIG.get(config);
    }
    
}
