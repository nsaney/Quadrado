package chairosoft.quadrado.game.resource.maproom;

import chairosoft.quadrado.game.QCollidable;
import chairosoft.quadrado.game.QDrawable;
import chairosoft.quadrado.game.resource.tileset.QTile;
import chairosoft.quadrado.game.resource.tileset.TileCodeLiteral;
import chairosoft.quadrado.game.resource.tileset.QTileset;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.graphics.*;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.util.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class QMapRoom<T extends Enum<T> & TileCodeLiteral<T>> extends QDrawable {
    
    ////// Constants //////
    public static final int Q_SPACE = 3;
    public static final MapRoomLayoutLoader LAYOUT_LOADER = new MapRoomLayoutLoader();
    
    
    ////// Instance Fields //////
    public final int backgroundColor;
    public final int widthTiles;
    public final int heightTiles;
    public final int widthPixels;
    public final int heightPixels;
    public final QTileset<T> tileset;
    public final DrawingImage defaultTileImage;
    public final QTile<?>[][] tileLayout;
    public final List<MapLink<T>> mapLinks;
    protected QTile<?> lastCollidingTile = null;
    
    
    ////// Constructor //////
    public QMapRoom(MapRoomConfig<T> _config, Supplier<QTileset<T>> _tilesetGetter, List<MapLink<T>> _mapLinks) {
        this.backgroundColor = _config.backgroundColor;
        this.tileset = _tilesetGetter.get();
        this.mapLinks = _mapLinks;
        
        this.defaultTileImage = UserInterfaceProvider.get().createDrawingImage(
            this.tileset.tileWidth,
            this.tileset.tileHeight,
            DrawingImage.Config.ARGB_8888
        );
        try (DrawingContext ctx = this.defaultTileImage.getContext()) {
            ctx.setColor(Color.WHITE);
            ctx.fillRect(0, 0, this.tileset.tileWidth, this.tileset.tileHeight);
            ctx.setColor(Color.BLACK);
            ctx.setFontFace(UserInterfaceProvider.get().createFontFace(FontFamily.MONOSPACED, FontStyle.PLAIN, 12));
            ctx.drawString("?", 1, this.tileset.tileHeight - 1);
        }
        
        T[] tileCodeValues = _config.tileCodeValuesGetter.get();
        Map<String, T> tileCodesByAlternateCode = TileCodeLiteral.getTileCodesByAlternateCode(tileCodeValues);
        Class<T> tileEnumClass = tileCodeValues[0].getDeclaringClass();
        final int CODE_LEN = tileCodeValues[0].name().length(); // assumes all tiles have the same length name
        List<String> layoutLinesList = LAYOUT_LOADER.loadOrNull(_config.mapRoomClass.getName());
        String[] layoutLines = layoutLinesList.stream()
            .filter(s -> s != null && s.length() >= CODE_LEN)
            .toArray(String[]::new)
        ;
        int _widthTiles = 0;
        this.tileLayout = new QTile<?>[layoutLines.length][];
        
        for (int y = 0, dy = 0; y < layoutLines.length; ++y, dy += tileset.tileHeight) {
            String line = layoutLines[y];
            int lineWidth = line.length() / CODE_LEN;
            _widthTiles = (lineWidth > _widthTiles) ? lineWidth : _widthTiles;
            QTile<?>[] tileRow = new QTile<?>[lineWidth];
            for (int x = 0, dx = 0, col = 0; x < tileRow.length; ++x, dx += tileset.tileWidth) {
                String tileCodeString = line.substring(col, col += CODE_LEN);
                QTile<T> tile = this.getTileFor(tileCodeString, tileEnumClass, tileCodesByAlternateCode);
                tile.translate(dx, dy);
                tileRow[x] = tile;
            }
            this.tileLayout[y] = tileRow;
        }
        this.widthTiles = _widthTiles;
        this.heightTiles = this.tileLayout.length;
        this.widthPixels = this.widthTiles * this.tileset.tileWidth;
        this.heightPixels = this.heightTiles * this.tileset.tileHeight;
        this.setImageFromLayout();
    }
    
    protected QTile<T> getTileFor(String tileCodeString, Class<T> tileEnumClass, Map<String, T> tileCodesByAlternateCode) {
        try {
            T tileCode = tileCodesByAlternateCode.get(tileCodeString);
            if (tileCode == null) { tileCode = Enum.valueOf(tileEnumClass, tileCodeString); }
            return this.tileset.createTile(tileCode);
        }
        catch (Exception ex) {
            return new QTile<>(null, this.defaultTileImage, new IntPoint2D[0]);
        }
    }
    
    
    ////// Instance Methods - Game //////
    public QTile<?> getLastCollidingTile() {
        return this.lastCollidingTile;
    }
    
    public boolean hasTileCollidingWith(QCollidable c) {
        this.setLastCollidingTileWith(c);
        return this.getLastCollidingTile() != null;
    }
    
    public void setLastCollidingTileWith(QCollidable c) {
        QTile result = null;
        IntPoint2D posC = c.getIntPosition();
        int colC = posC.x / this.tileset.tileWidth;
        int rowC = posC.y / this.tileset.tileHeight;
        
        int rowFirst       = rowC - Q_SPACE; if (rowFirst < 0) { rowFirst = 0; }
        int rowLastPlusOne = rowC + Q_SPACE; if (rowLastPlusOne > this.heightTiles) { rowLastPlusOne = this.heightTiles; }
        
        int colFirst       = colC - Q_SPACE; if (colFirst < 0) { colFirst = 0; }
        int colLastPlusOne = colC + Q_SPACE; if (colLastPlusOne > this.widthTiles) { colLastPlusOne = this.widthTiles; }
        
        outer:
        for (int row = rowFirst; row < rowLastPlusOne; ++row) {
            QTile<?>[] qtiles = this.tileLayout[row];
            for (int col = colFirst; col < colLastPlusOne && col < qtiles.length; ++col) {
                QTile<?> tile = qtiles[col];
                if (tile.collidesWith(c)) {
                    result = tile;
                    break outer;
                }
            }
        }
        this.lastCollidingTile = result;
    }
    
    public int getWrappedRowValue(int row) { return MathUtils.getWrappedValue(row, this.heightTiles); }
    public int getWrappedColValue(int col) { return MathUtils.getWrappedValue(col, this.widthTiles); }
    
    
    public MapLink getCollidingMapLinkOrNull(QCollidable c) {
        MapLink result = null;
        for (MapLink link : this.mapLinks) {
            // TODO: move get wrapped calls into the constructor
            int linkRow = this.getWrappedRowValue(link.row);
            int linkCol = this.getWrappedColValue(link.col);
            QTile<?> linkTile = this.tileLayout[linkRow][linkCol];
            if (linkTile.collidesWith(c)) {
                result = link;
                break;
            }
        }
        return result;
    }
    protected void setImageFromLayout() {
        DrawingImage _image = UserInterfaceProvider.get().createDrawingImage(
            this.widthPixels,
            this.heightPixels,
            DrawingImage.Config.ARGB_8888
        );
        try (DrawingContext ctx = _image.getContext()) {
            for (int y = 0, dy = 0; y < this.tileLayout.length; ++y, dy += this.tileset.tileHeight) {
                for (int x = 0, dx = 0; x < this.tileLayout[y].length; ++x, dx += this.tileset.tileWidth) {
                    this.tileLayout[y][x].drawToContext(ctx, dx, dy);
                }
            }
            this.image = _image;
        }
        catch (Exception ex) {
            System.err.println("DrawingContext error: " + ex);
        }
    }
    
    
    ////// Instance Methods - Editor //////
    public void changeQTileAtPosition(T tileCode, int row, int col) {
        if (row < 0 || this.tileLayout.length <= row) { return; }
        if (col < 0 || this.tileLayout[row].length <= col) { return; }
    
        this.tileLayout[row][col] = this.tileset.createTile(tileCode);
        this.setImageFromLayout();
    }
    
    
    ////// Static Methods - Declarative Syntax //////
    @SafeVarargs
    public static <T extends Enum<T> & TileCodeLiteral<T>> List<MapLink<T>> links(MapLink<T>... mapLinks) {
        return Arrays.asList(mapLinks);
    }
    
    public static <T extends Enum<T> & TileCodeLiteral<T>> MapLink<T> link(
        int row,
        int col,
        Supplier<QMapRoom<T>> mapRoomGetter,
        int row2,
        int col2
    ) {
        return new MapLink<>(row, col, mapRoomGetter, row2, col2);
    }
    
}
