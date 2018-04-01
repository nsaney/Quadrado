package chairosoft.quadrado.game.resource.maproom;

import chairosoft.quadrado.game.resource.tileset.TileCodeLiteral;

public class MapLink<T extends Enum<T> & TileCodeLiteral<T>> {
    
    ////// Instance Fields //////
    public final int row;
    public final int col;
    public final MapRoomConfig<T> targetMapConfig;
    public final int row2;
    public final int col2;
    
    
    ////// Constructor //////
    public MapLink(int _row, int _col, MapRoomConfig<T> _targetMapConfig, int _row2, int _col2)
    {
        this.row = _row;
        this.col = _col;
        this.targetMapConfig = _targetMapConfig;
        this.row2 = _row2;
        this.col2 = _col2;
    }
    
}
