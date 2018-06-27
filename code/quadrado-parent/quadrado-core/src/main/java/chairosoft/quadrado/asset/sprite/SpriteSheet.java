package chairosoft.quadrado.asset.sprite;

import chairosoft.quadrado.asset._resources.cache.ResourceCache;
import chairosoft.quadrado.ui.graphics.DrawingImage;

public class SpriteSheet {
    
    ////// Constants //////
    public static final SpriteSheetImageLoader IMAGE_LOADER = new SpriteSheetImageLoader();
    protected static final ResourceCache<SpriteSheet> SPRITE_SHEET_CACHE = new ResourceCache<>();
    
    
    ////// Instance Properties //////
    protected final DrawingImage[] imageArray;
    public DrawingImage getImage(int imageIndex) {
        return this.imageArray[imageIndex];
    }
    
    
    ////// Constructor //////
    protected SpriteSheet(SpriteSheetConfig config) {
        this.imageArray = IMAGE_LOADER.loadTiledImages(
            config.sheetName,
            config.transparencyRgb,
            config.spriteWidth,
            config.spriteHeight
        );
    }
    
    
    ////// Static Methods - Soft Map //////
    public static SpriteSheet loadFor(SpriteSheetConfig config) {
        return SPRITE_SHEET_CACHE.loadResource(config);
    }
    
}
