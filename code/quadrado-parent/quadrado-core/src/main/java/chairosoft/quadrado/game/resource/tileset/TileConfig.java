package chairosoft.quadrado.game.resource.tileset;

import chairosoft.quadrado.game.resource.literals.EnumCodedObjectImpl;
import chairosoft.quadrado.game.resource.literals.EnumLiteral;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.geom.Rectangle;

public class TileConfig<T extends Enum<T> & EnumLiteral<T>> extends EnumCodedObjectImpl<T> {
    
    ////// Instance Fields //////
    public final int imageIndex;
    public final IntPoint2D[] points;
    
    
    ////// Constructor //////
    protected TileConfig(T _code, int _imageIndex, Rectangle _rectangle) {
        super(_code);
        this.imageIndex = _imageIndex;
        this.points = _rectangle.getPoints();
    }
    
    protected TileConfig(T _code, int _imageIndex, IntPoint2D[] _points) {
        super(_code);
        this.imageIndex = _imageIndex;
        this.points = _points;
    }
    
}
