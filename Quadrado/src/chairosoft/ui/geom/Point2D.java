/* 
 * Nicholas Saney 
 * 
 * Created: January 21, 2015
 * Modified: January 28, 2015
 * 
 * Point2D.java
 * Point2D class definition
 * 
 */

package chairosoft.ui.geom;


/**
 * An object that has x and y coordinates.
 * Based on the {@link java.awt.geom.Point2D} specification and its subclasses in the AWT package.
 */
public abstract class Point2D implements Cloneable
{
    protected Point2D() { }
    
    // static methods
    public static double distanceSq(double x1, double y1, double x2, double y2)
    {
        double dx = x2 - x1; 
        double dy = y2 - y1; 
        return (dx * dx) + (dy * dy); 
    }
    
    public static double distance(double x1, double y1, double x2, double y2) 
    {
        return Math.sqrt(Point2D.distanceSq(x1, y1, x2, y2));
    }
    
    // abstract instance methods
    public abstract double getX();
    public abstract double getY();
    public abstract void setLocation(double x, double y);
    public abstract Object clone();
    
    // concrete instance methods
    public double distance(double px, double py) { return Point2D.distance(this.getX(), this.getY(), px, py); }
    public double distance(Point2D pt) { return Point2D.distance(this.getX(), this.getY(), pt.getX(), pt.getY()); }
    public double distanceSq(double px, double py) { return Point2D.distanceSq(this.getX(), this.getY(), px, py); }
    public double distanceSq(Point2D pt) { return Point2D.distanceSq(this.getX(), this.getY(), pt.getX(), pt.getY()); }
    public void setLocation(Point2D pt) { this.setLocation(pt.getX(), pt.getY()); }
    public FloatPoint2D asFloatPoint2D() { FloatPoint2D result = new FloatPoint2D(); result.setLocation(this); return result; }
    public IntPoint2D asIntPoint2D() { IntPoint2D result = new IntPoint2D(); result.setLocation(this); return result; }
    
    public boolean equals(Point2D that) { return this.getX() == that.getX() && this.getY() == that.getY(); }
    @Override public boolean equals(Object that) { return (that instanceof Point2D) && (this.equals((Point2D)that)); }
    @Override public int hashCode() { return (int)this.getX() ^ (int)this.getY(); }
}