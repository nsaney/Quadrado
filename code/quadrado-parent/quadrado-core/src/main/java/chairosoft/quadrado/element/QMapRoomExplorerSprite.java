/* 
 * Nicholas Saney 
 * 
 * Created: November 13, 2012
 * 
 * QMapRoomExplorerSprite.java
 * QMapRoomExplorerSprite class definition
 * 
 */

package chairosoft.quadrado.element;

import static chairosoft.quadrado.ui.input.direction.CompassDirection.*;

import chairosoft.quadrado.ui.input.direction.CompassDirection;
import chairosoft.quadrado.resource.maproom.QMapRoom;
import chairosoft.quadrado.resource.sprite.*;
import chairosoft.quadrado.ui.geom.FloatPoint2D;

import java.util.List;

/**
 * A QSprite that explores a map room and whose state is controlled
 * by selecting one of the nine CompassDirection options.
 */
public abstract class QMapRoomExplorerSprite<
    S extends Enum<S> & StateCodeLiteral<S>,
    B extends Enum<B> & BoundingShapeCodeLiteral<B>,
    A extends Enum<A> & AnimationCodeLiteral<A>
> extends QSprite<S, B, A> {
    
    ////// Constants //////
    public static final float MIN_MOVE_DISTANCE = 0.01f;
    private static final int[] POTENTIAL_ROTATION = {0, 1, -1};
    
    
    ////// Instance Fields //////
    protected CompassDirection facingDirection = SOUTH;
    protected CompassDirection currentDirection = CENTER;
    protected CompassDirection velocityDirection = CENTER;
    public volatile float speed = 1.0f;
    
    
    ////// Constructor //////
    protected QMapRoomExplorerSprite(
        SpriteSheetConfig spriteSheetConfig,
        List<BoundingShape<B>> boundingShapes,
        List<Animation<A>> animations,
        List<StateConfig<S, B, A>> stateConfigs
    ) {
        super(spriteSheetConfig, boundingShapes, animations, stateConfigs);
    }
    
    
    ////// Instance Methods - Abstract //////
    protected abstract void setNextState(CompassDirection nextDirection);
    
    
    ////// Instance Methods - Concrete //////
    public CompassDirection getFacingDirection() {
        return this.facingDirection;
    }
    
    public CompassDirection getCurrentDirection() {
        return this.currentDirection;
    }
    
    public CompassDirection getVelocityDirection() {
        return this.velocityDirection;
    }
    
    protected void setVelocity(CompassDirection nextDirection) {
        this.setXVelocity(nextDirection.X_UNIT * this.speed);
        this.setYVelocity(nextDirection.Y_UNIT_INVERT * this.speed);
    }
    
    public void setDirection(CompassDirection nextDirection) {
        this.setVelocity(nextDirection);
        if (nextDirection == this.currentDirection) {
            return;
        }
        
        this.setNextState(nextDirection);
        
        this.currentDirection = nextDirection;
        if (nextDirection != CENTER) {
            this.facingDirection = nextDirection;
        }
    }
    
    protected boolean canMoveWithDirectionInQMapRoom(
        CompassDirection direction,
        QMapRoom<?> maproom,
        FloatPoint2D originalPoint
    ) {
        boolean result = false;
        
        this.move(direction.X_UNIT, direction.Y_UNIT_INVERT, false);
        this.setPositionRounded();
        if (maproom.getCollidingMapLinkOrNull(this) != null || !maproom.hasTileCollidingWith(this)) {
            result = true;
        }
        this.setPosition(originalPoint.x, originalPoint.y);
        
        return result;
    }
    
    
    protected void setDirectionForMovingIn(QMapRoom<?> maproom) {
        if (this.currentDirection == CENTER) {
            this.velocityDirection = CENTER;
            return;
        }
        
        CompassDirection result = CENTER;
        
        FloatPoint2D originalPoint = this.getPosition();
        
        // check 3 directions: current and THEN one on either side
        for (int i = 0; i < 3; ++i) {
            int n = POTENTIAL_ROTATION[i];
            CompassDirection potentialResult = this.currentDirection.getCounterclockwiseRotation(n);
            
            if (this.canMoveWithDirectionInQMapRoom(potentialResult, maproom, originalPoint)) {
                result = potentialResult;
                break;
            }
        }
        
        this.velocityDirection = result;
    }
    
    public void moveOneFrameIn(QMapRoom<?> maproom) {
        this.setDirectionForMovingIn(maproom);
        this.setVelocity(this.velocityDirection);
        super.moveOneFrame();
    }
    
    public void resolveCollisionInQMapRoom(QMapRoom<?> maproom, boolean checkHorizontal, boolean checkVertical) {
        if ((this.lastMove.distance(0, 0) > MIN_MOVE_DISTANCE) && maproom.hasTileCollidingWith(this)) {
            FloatPoint2D p0 = this.getPosition();
            this.setPositionRounded();
            int undoneUnits = 0;
            double undoLimit = this.lastMove.getVectorLength();
            for (; undoneUnits < undoLimit && maproom.hasTileCollidingWith(this); ++undoneUnits) {
                this.undoLastMoveUnit();
            }
            if (undoneUnits >= undoLimit) {
                this.setPosition(p0.x, p0.y);
                this.undoLastMove();
            }
        }
    }
    
}