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
    
    public Polygon() {}
    public Polygon(FloatPoint2D... initialPoints)
    {
        Collections.addAll(this.points, initialPoints);
    }
    
    // instance methods
    public void reset() 
    {
        this.points.clear();
    }
    
    public void addPoint(int x, int y) 
    {
        this.points.add(new FloatPoint2D(x, y));
    }
    
    public void translate(int dx, int dy) 
    {
		if ((dx == 0) && (dy == 0)) { return; }
        for (FloatPoint2D p : this.points) { p.translate(dx, dy); }
    }
    
	public void translateFloat(float dx, float dy)
	{
		if ((dx == 0.0f) && (dy == 0.0f)) { return; }
		for (FloatPoint2D p : this.points) { p.translate(dx, dy); }
	}
}