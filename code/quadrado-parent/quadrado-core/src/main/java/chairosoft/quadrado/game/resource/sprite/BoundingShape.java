package chairosoft.quadrado.game.resource.sprite;

import chairosoft.quadrado.game.QCollidable;
import chairosoft.quadrado.game.resource.literals.EnumCodedObject;
import chairosoft.quadrado.ui.geom.IntPoint2D;

public class BoundingShape<B extends Enum<B> & BoundingShapeCodeLiteral<B>> extends EnumCodedObject<B> {
    
    ////// Instance Fields //////
    public final QCollidable shape;
    
    
    ////// Instance Properties //////
    protected final IntPoint2D offset;
    public IntPoint2D getOffset() { return new IntPoint2D(this.offset.x, this.offset.y); }
    
    
    ////// Constructor //////
    protected BoundingShape(B _code, int _x, int _y, QCollidable _shape) {
        super(_code);
        this.offset = new IntPoint2D(_x, _y);
        this.shape  = _shape;
    }
    
}