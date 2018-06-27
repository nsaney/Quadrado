package chairosoft.quadrado.asset.maproom;

import chairosoft.quadrado.asset._resources.cache.SuppliedResourceConfig;
import chairosoft.quadrado.asset.tileset.TileCodeLiteral;
import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;

public class MapRoomConfig<T extends Enum<T> & TileCodeLiteral<T>> extends SuppliedResourceConfig<QMapRoom<T>> {
    
    ////// Constructor //////
    public MapRoomConfig(
        ExceptionThrowingSupplier<QMapRoom<T>, ?> mapRoomGetter,
        Class<? extends QMapRoom<T>> mapRoomClass
    ) {
        super(mapRoomGetter, mapRoomClass);
    }
    
}
