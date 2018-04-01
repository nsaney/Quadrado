package chairosoft.quadrado.game.resource.maproom;

import chairosoft.quadrado.game.resource.tileset.TileCodeLiteral;

import java.util.function.Supplier;

public class MapLink<T extends Enum<T> & TileCodeLiteral<T>> {
    public final int row;
    public final int col;
    public final Supplier<QMapRoom<T>> mapRoomGetter;
    public final int row2;
    public final int col2;
    
    public MapLink(int _row, int _col, Supplier<QMapRoom<T>> _mapRoomGetter, int _row2, int _col2)
    { this.row = _row; this.col = _col; this.mapRoomGetter = _mapRoomGetter; this.row2 = _row2; this.col2 = _col2; }
}
