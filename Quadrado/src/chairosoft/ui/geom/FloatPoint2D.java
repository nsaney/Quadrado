/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * Modified: January 23, 2015
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
public class FloatPoint2D extends Point2D
{
    public float x = 0;
    public float y = 0;
    
    /** Creates a point with x = 0.0f and y = 0.0f. */
    public FloatPoint2D() { }
    
    /** Creates a point with the given x and y coordinates. 
     * @param _x the x coordinate value
     * @param _y the y coordinate value
     */
    public FloatPoint2D(float _x, float _y) { this.x = _x; this.y = _y; }
    
    // instance methods
    @Override public double getX() { return this.x; }
    @Override public double getY() { return this.y; }
    @Override public void setLocation(double x, double y) { this.x = (float)x; this.y = (float)y; }
    @Override public Object clone() { return new FloatPoint2D(this.x, this.y); }
    public void translate(float dx, float dy) { this.x += dx; this.y += dy; }
}