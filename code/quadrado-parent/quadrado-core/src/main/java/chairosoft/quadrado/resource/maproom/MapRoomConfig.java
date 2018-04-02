package chairosoft.quadrado.resource.maproom;

import chairosoft.quadrado.resource.loading.cache.SuppliedResourceConfig;
import chairosoft.quadrado.resource.tileset.TileCodeLiteral;
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
