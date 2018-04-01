package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.resource.literals.EnumLiteral;

public interface TileCodeLiteral<T extends Enum<T> & TileCodeLiteral<T>> extends EnumLiteral<T> {
    // nothing here right now
}
