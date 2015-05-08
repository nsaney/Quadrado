/* 
 * Nicholas Saney 
 * 
 * Created: February 17, 2013
 * Modified: January 22, 2015
 * 
 * QDrawable.java
 * QDrawable class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.Polygon;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

/**
 * An object which is meant to be drawn to a {@code DrawingContext} object with the 
 * {@code drawImage(DrawingImage,int,int)} method of the {@code DrawingContext} class.
 * 
 * @author Nicholas Saney
 */
public class QDrawable extends Polygon
{
	//
	// Instance Variables 
	// 
	
	protected DrawingImage image;
	
	
	// 
	// Constructors
	// 
	
	protected QDrawable() { super(); }
		
	
	//
	// Instance Methods 
	//
	
	/** 
	 * Gets this object's {@code image}.
	 * @return this object's {@code image}.
	 */
	public DrawingImage getImage() { return this.image; }
	
	
	/** 
	 * Sets this {@code QDrawable}'s {@code image} to {@code null}. 
	 */
	public void dispose() { this.image = null; }
	
	
	/**
	 * Conditionally calls the {@code drawImage(DrawingImage,int,int)} method on 
	 * the {@code context} object, with this object's {@code DrawingImage}.
	 * 
	 * @param context  the {@code Graphics} object to which to draw
	 * @param x         the x-location at which to draw the image's left edge
	 * @param y         the y-location at which to draw the image's top edge
	 */
	public void drawToContext(DrawingContext context, int x, int y)
	{
		if ((context != null) && (this.image != null)) 
		{ context.drawImage(this.image, x, y); }
	}
}