package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.resource.literals.EnumCodedObject;
import chairosoft.quadrado.game.resource.loading.ResourceCache;
import chairosoft.quadrado.game.resource.sprite.SpriteSheet;
import chairosoft.quadrado.game.resource.sprite.SpriteSheetConfig;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.geom.Rectangle;
import chairosoft.quadrado.ui.graphics.DrawingImage;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class QTileset<T extends Enum<T> & TileCodeLiteral<T>> {
    
    ////// Constants //////
    protected static final ResourceCache<QTileset<?>> TILESET_CACHE = new ResourceCache<>();
    
    
    ////// Instance Fields //////
    public final int tileWidth;
    public final int tileHeight;
    protected final EnumMap<T, TileConfig<T>> tileConfigsByCode;
    
    
    ////// Instance Properties /////
    protected final SpriteSheet tileSheet;
    
    
    ////// Constructor //////
    protected QTileset(SpriteSheetConfig config, List<TileConfig<T>> tileConfigs) {
        this.tileWidth = config.spriteWidth;
        this.tileHeight = config.spriteHeight;
        this.tileSheet = SpriteSheet.loadFor(config);
        this.tileConfigsByCode = EnumCodedObject.toMap(tileConfigs);
    }
    
    
    ////// Instance Methods //////
    public QTile<T> createTile(T code) {
        TileConfig<T> config = this.tileConfigsByCode.get(code);
        DrawingImage tileImage = this.tileSheet.getImage(config.imageIndex);
        return new QTile<>(config.code, tileImage, config.points);
    }
    
    
    ////// Static Methods - Soft Map //////
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & TileCodeLiteral<T>> QTileset<T> loadFor(TilesetConfig<T> config) {
        return (QTileset<T>)TILESET_CACHE.loadResource(config);
    }
    
    ////// Static Methods - Declarative Syntax //////
    @SafeVarargs
    public static <T extends Enum<T> & TileCodeLiteral<T>> List<TileConfig<T>> tiles(
        TileConfig<T>... tileConfigs
    ) {
        return Arrays.asList(tileConfigs);
    }
    
    public static <T extends Enum<T> & TileCodeLiteral<T>> TileConfig<T> tile(
        T code,
        int imageIndex
    ) {
        return new TileConfig<>(code, imageIndex, new IntPoint2D[0]);
    }
    
    public static <T extends Enum<T> & TileCodeLiteral<T>> TileConfig<T> tile(
        T code,
        int imageIndex,
        IntPoint2D... points
    ) {
        return new TileConfig<>(code, imageIndex, points);
    }
    
    public static IntPoint2D p(int x, int y) {
        return new IntPoint2D(x, y);
    }
    
    public static <T extends Enum<T> & TileCodeLiteral<T>> TileConfig<T> tile(
        T code,
        int imageIndex,
        Rectangle rectangle
    ) {
        return new TileConfig<>(code, imageIndex, rectangle);
    }
    
    public static Rectangle rectangle(int x, int y, int width, int height) {
        return new Rectangle(x, y, width, height);
    }
    
}
