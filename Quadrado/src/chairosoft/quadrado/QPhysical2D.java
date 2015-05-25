/* 
 * Nicholas Saney 
 * 
 * Created: February 22, 2013
 * 
 * QPhysical2D.java
 * QPhysical2D class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.FloatPoint2D;

import chairosoft.ui.graphics.DrawingContext;

import java.io.*; 
import java.util.*;

/**
 * A collidable object that has position, velocity, acceleration, 
 * and whose last change in position is tracked.
 */
public class QPhysical2D extends QCollidable implements QPositionedDrawable
{
	//
	// Static Constants
	//
	
	public static float SIN_45 = (float)Math.sin(Math.PI / 4);
	
	
	//
	// Instance Variables
	//
	
	protected FloatPoint2D position     = new FloatPoint2D(0, 0);
	protected FloatPoint2D velocity     = new FloatPoint2D(0, 0);
	protected FloatPoint2D acceleration = new FloatPoint2D(0, 0);
	protected FloatPoint2D lastMove     = new FloatPoint2D(0, 0);
	
	
	//
	// Constructor
	// 
	
	public QPhysical2D() {}
	public QPhysical2D(int x, int y) { this.setPosition(x, y); }
	
	
	//
	// Instance Methods
	// 
	
	//// Accessors
	
	public FloatPoint2D getPosition()     { return new FloatPoint2D(this.position.x,     this.position.y); }
	public FloatPoint2D getVelocity()     { return new FloatPoint2D(this.velocity.x,     this.velocity.y); }
	public FloatPoint2D getAcceleration() { return new FloatPoint2D(this.acceleration.x, this.acceleration.y); }
	public FloatPoint2D getLastMove()     { return new FloatPoint2D(this.lastMove.x,     this.lastMove.y); }
	
	
	//// Mutators
	
	// Position
    public final void setPosition(FloatPoint2D p) { this.setPosition(p.x, p.y); }
	public final void setPosition(float px, float py) 
	{
		this.setXPosition(px); 
        this.setYPosition(py);
		super.putFirstVertexAt(px, py);
	}
	public void setXPosition(float px) { this.position.x = px; }
	public void setYPosition(float py) { this.position.y = py; }
	
	// Velocity
	public final void setVelocity(FloatPoint2D v) { this.setVelocity(v.x, v.y); }
	public final void setVelocity(float vx, float vy) { this.setXVelocity(vx); this.setYVelocity(vy); }
	public void setXVelocity(float vx) { this.velocity.x = vx; }
	public void setYVelocity(float vy) { this.velocity.y = vy; }
	
	// Instantaneous Acceleration
    public final void instantaneouslyAccelerate(FloatPoint2D a) { this.instantaneouslyAccelerate(a.x, a.y); }
	public final void instantaneouslyAccelerate(float ax, float ay) { this.instantaneouslyAccelerateX(ax); this.instantaneouslyAccelerateY(ay); }
    public void instantaneouslyAccelerateX(float ax) { this.setXVelocity(this.velocity.x + ax); }
    public void instantaneouslyAccelerateY(float ay) { this.setYVelocity(this.velocity.y + ay); }
    
    // Acceleration
	public final void setAcceleration(FloatPoint2D a) { this.setAcceleration(a.x, a.y); }
	public final void setAcceleration(float ax, float ay) { this.setXAcceleration(ax); this.setYAcceleration(ay); }
    public void setXAcceleration(float ax) { this.acceleration.x = ax; }
    public void setYAcceleration(float ay) { this.acceleration.y = ay; }
	
	// Move
	protected void setLastMove(float dx, float dy) { this.lastMove.setLocation(dx, dy); }
	
	public void moveOneFrame() 
	{
		this.move(this.velocity);
		this.instantaneouslyAccelerate(this.acceleration);
	}
	public void repeatLastMove() { this.move(this.lastMove, false); }
	public void undoLastMove() { this.move(this.lastMove.getNegative(), false); }
	public void undoLastMoveUnit() { this.move(this.lastMove.getNegativeUnitVector(), false); }
    public void undoLastMoveDirectionWithLength(float length) { this.move(this.lastMove.getNegativeUnitVector().multipliedBy(length), false); }
	
	protected void move(FloatPoint2D p) { this.move(p, true); }
	protected void move(FloatPoint2D p, boolean setLast) { this.move(p.x, p.y, setLast); }
	protected void move(float dx, float dy) { this.move(dx, dy, true); }
	protected void move(float dx, float dy, boolean setLast) 
	{
		this.setPosition(this.position.x + dx, this.position.y + dy);
		if (setLast) { this.setLastMove(dx, dy); }
	}
	
	
	//// Extraclass Mutators 
	
	public void drawToContextAtOwnPosition(DrawingContext ctx)
    {
        this.drawToContext(ctx, Math.round(this.position.x), Math.round(this.position.y)); 
    }
}