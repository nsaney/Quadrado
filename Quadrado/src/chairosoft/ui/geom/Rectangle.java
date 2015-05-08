/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * Modified: January 19, 2015
 * 
 * Rectangle.java
 * Rectangle class definition
 * 
 */

package chairosoft.ui.geom;


/**
 * An object with an x-coordinate, a y-coordinate, a width, and a height.
 * Based on the {@link java.awt.Rectangle} specification in the AWT package.
 */
public class Rectangle
{
    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;
    
    public Rectangle() {}
    
    public Rectangle(int _width, int _height)
    {
        this.width = _width; 
        this.height = _height; 
    }
    
    public Rectangle(int _x, int _y, int _width, int _height)
    {
        this.x = _x;
        this.y = _y;
        this.width = _width; 
        this.height = _height; 
    }
    
    // instance methods
    
    public IntPoint2D[] getPoints()
    {
        IntPoint2D[] result = new IntPoint2D[4];
		result[0] = new IntPoint2D(this.x,              this.y);
		result[1] = new IntPoint2D(this.x + this.width, this.y);
		result[2] = new IntPoint2D(this.x + this.width, this.y + this.height);
		result[3] = new IntPoint2D(this.x,              this.y + this.height);
        return result;
    }
    
    public void translate(int dx, int dy) 
    {
        this.x += dx;
        this.y += dy;
    }
}