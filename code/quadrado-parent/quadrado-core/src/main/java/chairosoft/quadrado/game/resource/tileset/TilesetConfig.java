package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.resource.loading.SuppliedResourceConfig;
import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;

public class TilesetConfig<T extends Enum<T> & TileCodeLiteral<T>> extends SuppliedResourceConfig<QTileset<T>> {
    
    ////// Constructor //////
    public TilesetConfig(
        ExceptionThrowingSupplier<QTileset<T>, ?> tilesetGetter,
        Class<? extends QTileset<T>> tilesetClass
    ) {
        super(tilesetGetter, tilesetClass);
    }
    
}
