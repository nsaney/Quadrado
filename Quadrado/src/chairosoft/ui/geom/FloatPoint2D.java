/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * FloatPoint2D.java
 * FloatPoint2D class definition
 * 
 */

package chairosoft.ui.geom;


/**
 * An object that has x and y coordinates.
 * Based on the {@link java.awt.geom.Point2D} specification and its subclasses in the AWT package.
 */
public final class FloatPoint2D extends Point2D
{
    public float x = 0f;
    public float y = 0f;
    
    /** Creates a point with x = 0.0f and y = 0.0f. */
    public FloatPoint2D() { }
    
    /** Creates a point with the given x and y coordinates. 
     * @param _x the x coordinate value
     * @param _y the y coordinate value
     */
    public FloatPoint2D(float _x, float _y) { this.x = _x; this.y = _y; }
    
    // overridden instance methods
    @Override public double getX() { return this.x; }
    @Override public double getY() { return this.y; }
    @Override public void setLocation(double x, double y) { this.x = (float)x; this.y = (float)y; }
    @Override public Object clone() { return new FloatPoint2D(this.x, this.y); }
    
    // float-specific instance methods
    public void setLocation(float x, float y) { this.x = x; this.y = y; }
    public void translate(float dx, float dy) { this.x += dx; this.y += dy; }
    public void multiplyBy(float s) { this.x *= s; this.y *= s; }
    
    // fluent instance methods
    public FloatPoint2D translatedBy(float dx, float dy) { this.translate(dx, dy); return this; }
    public FloatPoint2D multipliedBy(float s) { this.multiplyBy(s); return this; }
    
    // new instance methods
    public FloatPoint2D getNegative() { return new FloatPoint2D(-this.x, -this.y); }
	public FloatPoint2D getUnitVector() 
	{
		float vlen = (float)this.getVectorLength();
		if (vlen == 0.0f) { return this; }
		return new FloatPoint2D(this.x / vlen, this.y / vlen);
	}
	public FloatPoint2D getNegativeUnitVector() 
	{
		float vlen = (float)this.getVectorLength();
		if (vlen == 0.0f) { return this; }
		return new FloatPoint2D(-this.x / vlen, -this.y / vlen); 
	}
}