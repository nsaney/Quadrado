package com.example.__resources;

import chairosoft.quadrado.game.resource.sprite.SpriteSheetConfig;
import chairosoft.quadrado.game.resource.tileset.TileCodeLiteral;
import chairosoft.quadrado.game.resource.tileset.QTileset;

import static com.example.__resources.TileSet_01.TileCode.*;


public class TileSet_01 extends QTileset<TileSet_01.TileCode> {
    
    public static final SpriteSheetConfig TILE_SHEET_CONFIG = new SpriteSheetConfig(
        "TileSheet_01",
        0xff00ff,
        16,
        16
    );
    
    public enum TileCode implements TileCodeLiteral<TileCode> {
        __("` "), _i("' "), Cf, E1, _0("`0"),
        N0, N1, N2, N3,
        W0, W1, W2, W3, W4, W5, W6, W7, W9, Wb, Wd, Wf,
        X0, X1, X2, X3, X4, X5, X6, X7, X9, Xb, Xd, Xf
        ;
        
        private final String alternateCode;
        public String getAlternateCode() { return this.alternateCode; }
        
        TileCode() { this.alternateCode = null; }
        TileCode(String _alternateCode) { this.alternateCode = _alternateCode; }
    }
    
    
    public TileSet_01() {
        super(
            TILE_SHEET_CONFIG,
            tiles(
                tile(__,  0),
                tile(_i,  1),
                tile(Cf,  2),
                tile(E1,  3, rectangle(2, 2, 12, 12)),
                tile(_0,  4),
                tile(N0, 60),
                tile(N1, 61),
                tile(N2, 62),
                tile(N3, 63),
                tile(W0, 18, rectangle(0, 0, 16, 16)),
                tile(W1, 10, rectangle(0, 0, 16, 16)),
                tile(W2,  9, rectangle(0, 0, 16, 16)),
                tile(W3,  8, rectangle(0, 0, 16, 16)),
                tile(W4, 16, rectangle(0, 0, 16, 16)),
                tile(W5, 24, rectangle(0, 0, 16, 16)),
                tile(W6, 25, rectangle(0, 0, 16, 16)),
                tile(W7, 26, rectangle(0, 0, 16, 16)),
                tile(W9, 12, rectangle(0, 0, 16, 16)),
                tile(Wb, 11, rectangle(0, 0, 16, 16)),
                tile(Wd, 19, rectangle(0, 0, 16, 16)),
                tile(Wf, 20, rectangle(0, 0, 16, 16)),
                tile(X0, 42, rectangle(0, 0, 16, 16)),
                tile(X1, 34, rectangle(0, 0, 16, 16)),
                tile(X2, 33, rectangle(0, 0, 16, 16)),
                tile(X3, 32, rectangle(0, 0, 16, 16)),
                tile(X4, 40, rectangle(0, 0, 16, 16)),
                tile(X5, 48, rectangle(0, 0, 16, 16)),
                tile(X6, 49, rectangle(0, 0, 16, 16)),
                tile(X7, 50, rectangle(0, 0, 16, 16)),
                tile(X9, 36, rectangle(0, 0, 16, 16)),
                tile(Xb, 35, rectangle(0, 0, 16, 16)),
                tile(Xd, 43, rectangle(0, 0, 16, 16)),
                tile(Xf, 44, rectangle(0, 0, 16, 16))
            )
        );
    }
    
}
