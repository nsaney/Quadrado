package chairosoft.quadrado.game.resource.maproom;

import chairosoft.quadrado.game.resource.loading.SuppliedResourceConfig;
import chairosoft.quadrado.game.resource.tileset.TileCodeLiteral;
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
