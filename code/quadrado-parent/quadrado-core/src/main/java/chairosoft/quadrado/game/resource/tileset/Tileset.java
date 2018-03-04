package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.resource.literals.EnumCodedObject;
import chairosoft.quadrado.game.resource.literals.EnumLiteral;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.geom.Rectangle;
import chairosoft.quadrado.ui.graphics.DrawingImage;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class Tileset<T extends Enum<T> & EnumLiteral<T>> {
    
    ////// Constants //////
    public static final TilesetImageLoader IMAGE_LOADER = new TilesetImageLoader();
    
    
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
    protected Tileset(int _tileWidth, int _tileHeight, int _transparencyRgb, List<TileConfig<T>> tileConfigs) {
        this.tileWidth = _tileWidth;
        this.tileHeight = _tileHeight;
        this.imageArray = IMAGE_LOADER.loadTiledImages(
            this.getClass(),
            _transparencyRgb,
            _tileWidth,
            _tileHeight
        );
        this.tileConfigsByCode = EnumCodedObject.toMap(tileConfigs);
    }
    
    
    ////// Instance Methods //////
    public Tile<T> createTile(T code) {
        TileConfig<T> config = this.tileConfigsByCode.get(code);
        DrawingImage tileImage = this.getImage(config.imageIndex);
        return new Tile<>(config.code, tileImage, config.points);
    }
    
    
    ////// Static Methods - Declarative Syntax //////
    @SafeVarargs
    public static <T extends Enum<T> & EnumLiteral<T>> List<TileConfig<T>> tiles(
        TileConfig<T>... tileConfigs
    ) {
        return Arrays.asList(tileConfigs);
    }
    
    public static <T extends Enum<T> & EnumLiteral<T>> TileConfig<T> tile(
        T code,
        int imageIndex
    ) {
        return new TileConfig<>(code, imageIndex, new IntPoint2D[0]);
    }
    
    public static <T extends Enum<T> & EnumLiteral<T>> TileConfig<T> tile(
        T code,
        int imageIndex,
        IntPoint2D... points
    ) {
        return new TileConfig<>(code, imageIndex, points);
    }
    
    public static IntPoint2D p(int x, int y) {
        return new IntPoint2D(x, y);
    }
    
    public static <T extends Enum<T> & EnumLiteral<T>> TileConfig<T> tile(
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
