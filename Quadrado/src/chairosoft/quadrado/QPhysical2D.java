/* 
 * Nicholas Saney 
 * 
 * Created: February 22, 2013
 * 
 * QPhysical2D.java
 * QPhysical2D abstract class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.FloatPoint2D;

import chairosoft.ui.graphics.DrawingContext;

import java.io.*; 
import java.util.*;

public abstract class QPhysical2D extends QCollidable implements QPositionedDrawable
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
	
	protected QPhysical2D() {}
	protected QPhysical2D(int x, int y) { this.setPosition(x, y); }
	
	
	//
	// Static Methods
	// 
	
	public static FloatPoint2D getNegativeVector(FloatPoint2D p) 
	{
		return new FloatPoint2D(-p.x, -p.y); 
	}
	
	public static FloatPoint2D getUnitVector(FloatPoint2D p) 
	{
		float vlen = (float)p.distance(0, 0);
		if (vlen == 0.0f) { return p; }
		return new FloatPoint2D(p.x / vlen, p.y / vlen);
	}
	
	public static FloatPoint2D getNegativeUnitVector(FloatPoint2D p) 
	{
		float vlen = (float)p.distance(0, 0);
		if (vlen == 0.0f) { return p; }
		return new FloatPoint2D(-p.x / vlen, -p.y / vlen); 
	}
    
    public static FloatPoint2D getVectorOfLength(FloatPoint2D p, float length)
    {
        FloatPoint2D result = getUnitVector(p);
        result.x *= length;
        result.y *= length;
        return result;
    }
	
	
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
	public void setPosition(float x, float y) 
	{
		this.position.setLocation(x, y); 
		super.putFirstVertexAt(x, y);
	}
	
	// Velocity
	protected void setXVelocity(float vx) { this.velocity.x = vx; }
	protected void setYVelocity(float vy) { this.velocity.y = vy; }
	public void setVelocity(FloatPoint2D p) { this.setVelocity(p.x, p.y); }
	public void setVelocity(float vx, float vy) { this.setXVelocity(vx); this.setYVelocity(vy); }
	public void setZeroVelocity() { this.setVelocity(0, 0); }
	
	// Acceleration
	public void instantaneouslyAccelerate(FloatPoint2D p) { this.instantaneouslyAccelerate(p.x, p.y); }
	public void instantaneouslyAccelerate(float ax, float ay) { this.setVelocity(this.velocity.x + ax, this.velocity.y + ay); }
	public void setAcceleration(float ax, float ay) { this.acceleration.setLocation(ax, ay); }
	
	// Move
	protected void setLastMove(float dx, float dy) { this.lastMove.setLocation(dx, dy); }
	
	public void moveOneFrame() 
	{
		this.move(this.velocity);
		this.instantaneouslyAccelerate(this.acceleration);
	}
	public void repeatLastMove() { this.move(this.lastMove, false); }
	public void undoLastMove() { this.move(getNegativeVector(this.lastMove), false); }
	public void undoLastMoveUnit() { this.move(getNegativeUnitVector(this.lastMove), false); }
    public void undoLastMoveHalfUnit() { this.undoLastMoveDirectionWithLength(0.50f); }
    public void undoLastMoveQuarterUnit() { this.undoLastMoveDirectionWithLength(0.25f); }
    public void undoLastMoveDirectionWithLength(float length) { this.move(getVectorOfLength(this.lastMove, length), false); }
	
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