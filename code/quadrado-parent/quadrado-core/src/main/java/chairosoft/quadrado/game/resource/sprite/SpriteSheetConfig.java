package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.resource.loading.ResourceConfig;


public class SpriteSheetConfig extends ResourceConfig {
    
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
        super(_sheetName, _transparencyRgb, _spriteWidth, _spriteHeight);
        this.sheetName = _sheetName;
        this.transparencyRgb = _transparencyRgb;
        this.spriteWidth = _spriteWidth;
        this.spriteHeight = _spriteHeight;
    }
    
}
