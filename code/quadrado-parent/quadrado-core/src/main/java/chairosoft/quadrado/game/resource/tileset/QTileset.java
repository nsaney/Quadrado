package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.resource.literals.EnumCodedObject;
import chairosoft.quadrado.game.resource.loading.SoftMap;
import chairosoft.quadrado.game.resource.sprite.SpriteSheetConfig;
import chairosoft.quadrado.game.resource.sprite.SpriteSheetImageLoader;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.geom.Rectangle;
import chairosoft.quadrado.ui.graphics.DrawingImage;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class QTileset<T extends Enum<T> & TileCodeLiteral<T>> {
    
    ////// Constants //////
    public static final SpriteSheetImageLoader IMAGE_LOADER = new SpriteSheetImageLoader();
    protected static final SoftMap.ByDefaultConstructor<QTileset<?>> TILESETS_BY_CLASS = new SoftMap.ByDefaultConstructor<>();
    
    
    ////// Instance Fields //////
    public final int tileWidth;
    public final int tileHeight;
    protected final EnumMap<T, TileConfig<T>> tileConfigsByCode;
    
    
    ////// Instance Properties /////
    protected final DrawingImage[] imageArray;
    public DrawingImage getImage(int imageIndex) {
        return this.imageArray[imageIndex];
    }
    
    
    ////// Constructor //////
    protected QTileset(SpriteSheetConfig tileSheetConfig, List<TileConfig<T>> tileConfigs) {
        this.tileWidth = tileSheetConfig.spriteWidth;
        this.tileHeight = tileSheetConfig.spriteHeight;
        this.imageArray = IMAGE_LOADER.loadTiledImages(
            tileSheetConfig.sheetName,
            tileSheetConfig.transparencyRgb,
            tileSheetConfig.spriteWidth,
            tileSheetConfig.spriteHeight
        );
        this.tileConfigsByCode = EnumCodedObject.toMap(tileConfigs);
    }
    
    
    ////// Instance Methods //////
    public QTile<T> createTile(T code) {
        TileConfig<T> config = this.tileConfigsByCode.get(code);
        DrawingImage tileImage = this.getImage(config.imageIndex);
        return new QTile<>(config.code, tileImage, config.points);
    }
    
    
    ////// Static Methods - Soft Map //////
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & TileCodeLiteral<T>> QTileset<T> get(Class<? extends QTileset<T>> clazz) {
        return (QTileset<T>) TILESETS_BY_CLASS.get(clazz);
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
