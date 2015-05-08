/* 
 * Nicholas Saney 
 * 
 * Created: November 14, 2013
 * 
 * QCompassDirection.java
 * QCompassDirection enum definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.FloatPoint2D;

import java.io.*; 
import java.util.*;

public enum QCompassDirection
{
	//
	// Enum Constants
	//
	
	EAST      (0, +1,  0, CardinalAlignment.HORIZONTAL),
	NORTHEAST (1, +1, +1, CardinalAlignment.NONE),
	NORTH     (2,  0, +1, CardinalAlignment.VERTICAL),
	NORTHWEST (3, -1, +1, CardinalAlignment.NONE),
	WEST      (4, -1,  0, CardinalAlignment.HORIZONTAL),
	SOUTHWEST (5, -1, -1, CardinalAlignment.NONE),
	SOUTH     (6,  0, -1, CardinalAlignment.VERTICAL),
	SOUTHEAST (7, +1, -1, CardinalAlignment.NONE),
	CENTER    (8,  0,  0, CardinalAlignment.NONE);
	
	
	//
	// Inner Static Enum
	//
	
	private static enum CardinalAlignment { NONE, HORIZONTAL, VERTICAL }
	
	
	//
	// Static Fields
	//
	
	private static QCompassDirection[] VALUES = 
	{
		EAST, 
		NORTHEAST, 
		NORTH, 
		NORTHWEST, 
		WEST, 
		SOUTHWEST, 
		SOUTH, 
		SOUTHEAST, 
		CENTER
	};
	
	private static int NUMBER_OF_NON_ROTATING_VALUES = 1;
	private static int NUMBER_OF_ROTATING_VALUES = VALUES.length - NUMBER_OF_NON_ROTATING_VALUES;
	private static int HALF_NUMBER_OF_ROTATING_VALUES = NUMBER_OF_ROTATING_VALUES / 2;
	
	// private static void checkForEvenRotatingValues()
	// throws Exception
	// {
	// 	if (0 != (NUMBER_OF_ROTATING_VALUES % 2))
	// 	{
	// 		throw new Exception("Must have even number of rotating values for QCompassDirection.");
	// 	}
	// }
	
	//
	// Instance Fields
	//
	
	public final int VALUE;
	public final int X;
	public final int Y;
	public final int X_INVERT;
	public final int Y_INVERT;
	public final float X_UNIT;
	public final float Y_UNIT;
	public final float X_UNIT_INVERT;
	public final float Y_UNIT_INVERT;
	public final boolean IS_CARDINAL;
	public final boolean IS_HORIZONTAL;
	public final boolean IS_VERTICAL;
	
	
	//
	// Constructor
	//
	
	private QCompassDirection(int _value, int _x, int _y, CardinalAlignment _alignment)
	{
		this.VALUE         = _value; 
		
		this.X             = _x;
		this.Y             = _y;
		this.X_INVERT      = -this.X;
		this.Y_INVERT      = -this.Y;
		
		FloatPoint2D unit = QPhysical2D.getUnitVector(new FloatPoint2D(this.X, this.Y));
		
		this.X_UNIT        = unit.x;
		this.Y_UNIT        = unit.y;
		this.X_UNIT_INVERT = -this.X_UNIT;
		this.Y_UNIT_INVERT = -this.Y_UNIT;
		
		this.IS_CARDINAL   = (_alignment != CardinalAlignment.NONE);
		this.IS_HORIZONTAL = (_alignment == CardinalAlignment.HORIZONTAL);
		this.IS_VERTICAL   = (_alignment == CardinalAlignment.VERTICAL);
	}
	
	
	//
	// Static Methods
	//
	
	public static QCompassDirection getByValue(int _value) { return VALUES[_value]; }
	
	public static QCompassDirection combineDirections
	(
		QCompassDirection directionA, 
		QCompassDirection directionB
	)
	{
		if (directionA == directionB) { return directionA; }
		if (directionA.VALUE < directionB.VALUE) { return combineDirections_usingSwitch(directionB, directionA); }
		if (directionA == CENTER) { return directionB; }
		
		// at this point:
		// * the inputs are not the same
		// * the inputs are non-CENTER, and 
		// * A has a greater VALUE than B
		
		int difference = directionA.VALUE - directionB.VALUE;
		if (difference == HALF_NUMBER_OF_ROTATING_VALUES) { return CENTER; }
		
		int average = (directionA.VALUE + directionB.VALUE) / 2;
		if (difference > HALF_NUMBER_OF_ROTATING_VALUES) 
		{
			return getByValue((average + HALF_NUMBER_OF_ROTATING_VALUES) % NUMBER_OF_ROTATING_VALUES);
		}
		return getByValue(average);
	}
	
	public static QCompassDirection combineDirections_usingSwitch
	(
		QCompassDirection directionA, 
		QCompassDirection directionB
	)
	{
		if (directionA == directionB) { return directionA; }
		if (directionA.VALUE < directionB.VALUE) { return combineDirections_usingSwitch(directionB, directionA); }
		if (directionA == CENTER) { return directionB; }
		
		// at this point:
		// * the inputs are not the same
		// * the inputs are non-CENTER, and 
		// * A has a greater VALUE than B
		
		switch (directionA)
		{
			case NORTHEAST: return EAST;
				// switch (directionB)
				// {
				// 	case EAST: return EAST;
				// }
				// break;
			case NORTH:
				switch (directionB)
				{
					case EAST: return NORTHEAST;
					case NORTHEAST: return NORTH;
				}
				break;
			case NORTHWEST:
				switch (directionB)
				{
					case EAST: return NORTHEAST;
					case NORTHEAST: // passthrough
					case NORTH: return NORTH;
				}
				break;
			case WEST:
				switch (directionB)
				{
					//case EAST: return CENTER;
					case NORTHEAST: return NORTH;
					case NORTH: return NORTHWEST;
					case NORTHWEST: return WEST;
				}
				break;
			case SOUTHWEST:
				switch (directionB)
				{
					case EAST: return SOUTH;
					//case NORTHEAST: return CENTER;
					case NORTH: return NORTHWEST;
					case NORTHWEST: // passthrough
					case WEST: return WEST;
				}
				break;
			case SOUTH:
				switch (directionB)
				{
					case EAST: // passthrough
					case NORTHEAST: return SOUTHEAST;
					//case NORTH: return CENTER;
					case NORTHWEST: return WEST;
					case WEST: return SOUTHWEST;
					case SOUTHWEST: return SOUTH;
				}
				break;
			case SOUTHEAST:
				switch (directionB)
				{
					case EAST: // passthrough
					case NORTHEAST: 
					case NORTH: return EAST;
					//case NORTHWEST: return CENTER;
					case WEST: return SOUTHWEST;
					case SOUTHWEST: // passthrough
					case SOUTH: return SOUTH;
				}
				break;
		}
		
		// default for either (shouldn't be needed)
		return CENTER;
	}
	
	
	
	//
	// Instance Methods 
	//
	
	public QCompassDirection getClockwiseNeighbor() { return this.getClockwiseRotation(1); }
	public QCompassDirection getCounterclockwiseNeighbor() { return this.getCounterclockwiseRotation(1); }
	
	public QCompassDirection getClockwiseRotation(int units) { return this.getCounterclockwiseRotation(-units); }
	public QCompassDirection getCounterclockwiseRotation(int units) 
	{
		if (units < 0) { return getCounterclockwiseRotation(units + NUMBER_OF_ROTATING_VALUES); }
		if (units == 0) { return this; }
		
		QCompassDirection result = this;
		if (this.VALUE <= NUMBER_OF_ROTATING_VALUES) 
		{
			result = getByValue((this.VALUE + units) % NUMBER_OF_ROTATING_VALUES);
		}
		return result;
	}
}