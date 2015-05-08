/* 
 * Nicholas Saney 
 * 
 * Created: February 11, 2013
 * 
 * QAbstractMapRoom.java
 * QAbstractMapRoom abstract class definition
 * 
 */

package chairosoft.quadrado;


public abstract class QAbstractMapRoom extends QDrawable
{
	
	//
	// Instance Variables
	//
	
	protected QTile lastCollidingTile;
	
	
	//
	// Instance Methods 
	//
	
	public QTile getLastCollidingTile() { return this.lastCollidingTile; }
	protected abstract void setLastCollidingTileWith(QCollidable c);
	
	public boolean hasTileCollidingWith(QCollidable c) 
	{
	    this.setLastCollidingTileWith(c);
	    return (this.getLastCollidingTile() != null); 
	}
}