package chairosoft.quadrado.resource.sprite;

import chairosoft.quadrado.resource.loading.cache.ResourceConfig;

public class SpriteSheetConfig extends ResourceConfig<SpriteSheet> {
    
    ////// Instance Fields //////
    public final String sheetName;
    public final int transparencyRgb;
    public final int spriteWidth;
    public final int spriteHeight;
    
    
    ////// Constructor //////
    public SpriteSheetConfig(
        String _sheetName,
        int _transparencyRgb,
        int _spriteWidth,
        int _spriteHeight
    ) {
        super(null, _sheetName, _transparencyRgb, _spriteWidth, _spriteHeight);
        this.sheetName = _sheetName;
        this.transparencyRgb = _transparencyRgb;
        this.spriteWidth = _spriteWidth;
        this.spriteHeight = _spriteHeight;
    }
    
    
    ////// Instance Methods //////
    @Override
    public SpriteSheet getResource() {
        return new SpriteSheet(this);
    }
    
}
