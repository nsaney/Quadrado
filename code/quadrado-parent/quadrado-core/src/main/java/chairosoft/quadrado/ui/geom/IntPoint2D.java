/* 
 * Nicholas Saney 
 * 
 * Created: January 21, 2015
 * 
 * IntPoint2D.java
 * IntPoint2D class definition
 * 
 */

package chairosoft.quadrado.ui.geom;


/**
 * An object that has x and y coordinates.
 * Based on the {@link java.awt.geom.Point2D} specification and its subclasses in the AWT package.
 */
public class IntPoint2D extends Point2D
{
    public int x = 0;
    public int y = 0;
    
    /** Creates a point with x = 0.0f and y = 0.0f. */
    public IntPoint2D() { }
    
    /** Creates a point with the given x and y coordinates. 
     * @param _x the x coordinate value
     * @param _y the y coordinate value
     */
    public IntPoint2D(int _x, int _y) { this.x = _x; this.y = _y; }
    
    // instance methods
    @Override public double getX() { return this.x; }
    @Override public double getY() { return this.y; }
    @Override public void setLocation(double x, double y) { this.x = (int)x; this.y = (int)y; }
    @Override public Object clone() { return new FloatPoint2D(this.x, this.y); }
    public void translate(int dx, int dy) { this.x += dx; this.y += dy; }
}