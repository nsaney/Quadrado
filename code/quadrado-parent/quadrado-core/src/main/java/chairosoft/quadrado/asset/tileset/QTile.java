package chairosoft.quadrado.asset.tileset;

import chairosoft.quadrado.element.QCollidable;
import chairosoft.quadrado.asset._literals.EnumCodedObject;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.graphics.DrawingImage;

public class QTile<T extends Enum<T> & TileCodeLiteral<T>> extends QCollidable implements EnumCodedObject<T> {
    
    ////// Instance Properties //////
    public final T code;
    @Override public T getCode() { return this.code; }
    
    
    ////// Constructor //////
    public QTile(T _code, DrawingImage _image, IntPoint2D[] _points) {
        this.code = _code;
        this.image = _image;
        this.addAllPoints(_points);
    }
    
}
