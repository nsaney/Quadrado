/* 
 * Nicholas Saney 
 * 
 * Created: November 13, 2012
 * 
 * QMapRoomExplorerSprite.java
 * QMapRoomExplorerSprite class definition
 * 
 */

package chairosoft.quadrado;

import static chairosoft.quadrado.QCompassDirection.*;
import chairosoft.quadrado.*;

import chairosoft.ui.geom.Point2D;
import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.IntPoint2D;
import chairosoft.ui.geom.Rectangle;

import java.io.*; 
import java.util.*;

/**
 * A QSprite that explores a map room and whose state is controlled 
 * by selecting one of the nine QCompassDirection options.
 */
public abstract class QMapRoomExplorerSprite extends QSprite
{
	//
	// Instance Fields
	//
	
    protected QCompassDirection facingDirection = SOUTH;
	protected QCompassDirection currentDirection = CENTER;
	protected QCompassDirection velocityDirection = CENTER;
    public volatile float speed = 1.0f;
	
	
	//
	// Constructor
	//
	
	/**
	 * The constructor for maproom explorer sprites.
	 * 
	 * @param protagonistCode The sprite code for the requested explorer.
	 */
	protected QMapRoomExplorerSprite(String protagonistCode) { super(protagonistCode); }
	
	
	//
	// Instance Methods
	//
	
	public QCompassDirection getFacingDirection() { return this.facingDirection; }
	public QCompassDirection getCurrentDirection() { return this.currentDirection; }
	public QCompassDirection getVelocityDirection() { return this.velocityDirection; }
	
	protected void setVelocity(QCompassDirection nextDirection)
	{
		this.setXVelocity(nextDirection.X_UNIT * this.speed);
		this.setYVelocity(nextDirection.Y_UNIT_INVERT * this.speed);
	}
	
    protected abstract void setNextState(QCompassDirection nextDirection);
	public void setDirection(QCompassDirection nextDirection)
	{
		this.setVelocity(nextDirection);
		if (nextDirection == this.currentDirection) { return; }
		
        this.setNextState(nextDirection);
		
		this.currentDirection = nextDirection;
        if (nextDirection != CENTER)
        {
            this.facingDirection = nextDirection;
        }
	}
	
	protected boolean canMoveWithDirectionInQMapRoom
	(
		QCompassDirection direction, 
		QMapRoom qmaproom, 
		FloatPoint2D originalPoint
	)
	{
		boolean result = false;
		
		this.move(direction.X_UNIT, direction.Y_UNIT_INVERT, false);
		this.setPositionRounded();
		if (qmaproom.getCollidingMapLinkOrNull(this) != null || !qmaproom.hasTileCollidingWith(this)) 
        {
            result = true; 
        }
		this.setPosition(originalPoint.x, originalPoint.y);
		
		return result;
	}
	
    private static int[] potentialRotation = { 0, 1, -1 };
	protected void setDirectionForMovingIn(QMapRoom qmaproom)
	{
		if (this.currentDirection == CENTER) { this.velocityDirection = CENTER; return; }
		
		QCompassDirection result = CENTER;
		
		FloatPoint2D originalPoint = this.getPosition();
		
		// check 3 directions: current and THEN one on either side
		for (int i = 0; i < 3; ++i)
		{
			int n = potentialRotation[i];
			QCompassDirection potentialResult = this.currentDirection.getCounterclockwiseRotation(n);
			
			if (this.canMoveWithDirectionInQMapRoom(potentialResult, qmaproom, originalPoint)) 
			{
				result = potentialResult;
				break;
			}
		}
		
		this.velocityDirection = result;
	}
	
	public void moveOneFrameIn(QMapRoom qmaproom)
	{
		this.setDirectionForMovingIn(qmaproom);
		this.setVelocity(this.velocityDirection);
		super.moveOneFrame();
	}
}