package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.loading.ResourceConfig;


public class SpriteSheetConfig extends ResourceConfig {
    
    ////// Instance Fields //////
    public final Class<? extends QSprite> spriteClass;
    public final int transparencyRgb;
    public final int spriteWidth;
    public final int spriteHeight;
    
    
    ////// Constructor //////
    public SpriteSheetConfig(
        Class<? extends QSprite> _spriteClass,
        int _transparencyRgb,
        int _spriteWidth,
        int _spriteHeight
    ) {
        super(_spriteClass, _transparencyRgb, _spriteWidth, _spriteHeight);
        this.spriteClass = _spriteClass;
        this.transparencyRgb = _transparencyRgb;
        this.spriteWidth = _spriteWidth;
        this.spriteHeight = _spriteHeight;
    }
    
}
