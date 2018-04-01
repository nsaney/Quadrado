package chairosoft.quadrado.game.resource.maproom;

import chairosoft.quadrado.game.resource.loading.ResourceConfig;
import chairosoft.quadrado.game.resource.tileset.TileCodeLiteral;

import java.util.function.Supplier;

public class MapRoomConfig<T extends Enum<T> & TileCodeLiteral<T>> extends ResourceConfig {
    
    ////// Instance Fields //////
    public final Class<? extends MapRoom<? extends T>> mapRoomClass;
    public final Supplier<T[]> tileCodeValuesGetter;
    public final int backgroundColor;
    
    
    ////// Constructor //////
    public MapRoomConfig(Class<? extends MapRoom<T>> _mapRoomClass, Supplier<T[]> _tileCodeValuesGetter, int _backgroundColor) {
        super(_mapRoomClass, _tileCodeValuesGetter, _backgroundColor);
        this.mapRoomClass = _mapRoomClass;
        this.tileCodeValuesGetter = _tileCodeValuesGetter;
        this.backgroundColor = _backgroundColor;
    }
    
}
