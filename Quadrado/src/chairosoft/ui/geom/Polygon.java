/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * Polygon.java
 * Polygon class definition
 * 
 */

package chairosoft.ui.geom;

import java.util.Collections;
import java.util.ArrayList;

/**
 * An object that has some number of vertices.
 * Based on the {@link java.awt.Polygon} specification in the AWT package.
 */
public class Polygon
{
    public final ArrayList<FloatPoint2D> points = new ArrayList<>();
    
    protected boolean boundsValid = false;
    protected final Bounds bounds = new Bounds();
    public Bounds getBounds()
    {
        if (!this.boundsValid)
        {
            FloatPoint2D p0 = this.points.get(0);
            float minX = p0.x;
            float minY = p0.y;
            float maxX = p0.x;
            float maxY = p0.y;
            for (int i = 1; i < this.points.size(); ++i)
            {
                FloatPoint2D p = this.points.get(i);
                if (p.x < minX) { minX = p.x; } else if (p.x > maxX) { maxX = p.x; }
                if (p.y < minY) { minY = p.y; } else if (p.y > maxY) { maxY = p.y; }
            }
            this.bounds.x = minX;
            this.bounds.y = minY;
            this.bounds.width = (maxX - minX);
            this.bounds.height = (maxY - minY);
            this.boundsValid = true;
        }
        return this.bounds;
    }
    
    
    public Polygon() {}
    public Polygon(FloatPoint2D... initialPoints)
    {
        Collections.addAll(this.points, initialPoints);
    }
    
    // instance methods
    public void reset() 
    {
        this.points.clear();
        this.boundsValid = false;
    }
    
    public void addPoint(int x, int y) 
    {
        this.points.add(new FloatPoint2D(x, y));
        this.boundsValid = false;
    }
    
    public void addPoint(float x, float y)
    {
        this.points.add(new FloatPoint2D(x, y));
        this.boundsValid = false;
    }
    
    public void translate(int dx, int dy) 
    {
        if ((dx == 0) && (dy == 0)) { return; }
        for (FloatPoint2D p : this.points) { p.translate(dx, dy); }
        this.boundsValid = false;
    }
    
    public void translateFloat(float dx, float dy)
    {
        if ((dx == 0.0f) && (dy == 0.0f)) { return; }
        for (FloatPoint2D p : this.points) { p.translate(dx, dy); }
        this.boundsValid = false;
    }
    
    
    //
    // static inner classes
    //
    
    public static class Bounds
    {
        public float x = 0;
        public float y = 0;
        public float width = 0;
        public float height = 0;
            
        public boolean containsPoint(float dx, float dy)
        {
            return dx > this.x
                && dx <= (this.x + this.width)
                && dy > this.y
                && dy <= (this.y + this.height)
            ;
        }
    }
}