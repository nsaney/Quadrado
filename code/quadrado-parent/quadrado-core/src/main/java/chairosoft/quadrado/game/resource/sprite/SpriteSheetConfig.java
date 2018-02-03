package chairosoft.quadrado.game.resource.sprite;

import java.util.Arrays;

public class SpriteSheetConfig {
    
    ////// Instance Fields //////
    public final Class<? extends QSprite<?, ?, ?>> spriteClass;
    public final int transparencyRgb;
    public final int spriteWidth;
    public final int spriteHeight;
    private final int hashCode;
    
    
    ////// Constructor //////
    public SpriteSheetConfig(
        Class<? extends QSprite<?, ?, ?>> _spriteClass,
        int _transparencyRgb,
        int _spriteWidth,
        int _spriteHeight
    ) {
        this.spriteClass = _spriteClass;
        this.transparencyRgb = _transparencyRgb;
        this.spriteWidth = _spriteWidth;
        this.spriteHeight = _spriteHeight;
        this.hashCode = Arrays.asList(_spriteClass, _spriteHeight, _spriteWidth, _spriteHeight).hashCode();
    }
    
    
    ////// Instance Methods //////
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpriteSheetConfig)) { return false; }
        SpriteSheetConfig that = (SpriteSheetConfig)obj;
        return this.hashCode == that.hashCode
            && this.spriteClass == that.spriteClass
            && this.spriteWidth == that.spriteWidth
            && this.spriteHeight == that.spriteHeight
            && this.transparencyRgb == that.transparencyRgb
        ;
    }
}
