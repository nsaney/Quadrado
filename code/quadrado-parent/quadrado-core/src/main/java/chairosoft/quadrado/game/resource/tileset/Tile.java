package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.QCollidable;
import chairosoft.quadrado.game.resource.literals.EnumCodedObject;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.graphics.DrawingImage;

public class Tile<T extends Enum<T> & TileCodeLiteral<T>> extends QCollidable implements EnumCodedObject<T> {
    
    ////// Instance Properties //////
    public final T code;
    @Override public T getCode() { return this.code; }
    
    
    ////// Constructor //////
    protected Tile(T _code, DrawingImage _image, IntPoint2D[] _points) {
        this.code = _code;
        this.image = _image;
        this.addAllPoints(_points);
    }
    
}
