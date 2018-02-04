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
        DrawingImage[] _imageArray;
        DrawingImage spriteSheetImageRaw = IMAGE_LOADER.wrappedLoad(config.spriteClass);
        if (spriteSheetImageRaw == null) {
            _imageArray = new DrawingImage[0];
        }
        else {
            DrawingImage spriteSheetImage = spriteSheetImageRaw.getCloneWithTransparency(config.transparencyRgb);
            int spriteSheetHeight = spriteSheetImage.getHeight();
            int spriteSheetWidth = spriteSheetImage.getWidth();
            int verticalSpriteCount = spriteSheetHeight / config.spriteHeight;
            int horizontalSpriteCount = spriteSheetWidth / config.spriteWidth;
            _imageArray = new DrawingImage[verticalSpriteCount * horizontalSpriteCount];
            for (int i = 0, y = 0; y < spriteSheetHeight; y += config.spriteHeight) {
                for (int x = 0; x < spriteSheetWidth; x += config.spriteWidth, ++i) {
                    DrawingImage spriteImage = spriteSheetImage.getImmutableSubimage(
                        x,
                        y,
                        config.spriteWidth,
                        config.spriteHeight
                    );
                    _imageArray[i] = spriteImage;
                }
            }
        }
        this.imageArray = _imageArray;
    }
    
    
    ////// Static Methods //////
    public static SpriteSheet get(SpriteSheetConfig config) {
        return SPRITE_SHEETS_BY_CONFIG.get(config);
    }
    
}
