/* 
 * Nicholas Saney 
 * 
 * Created: January 18, 2013
 * 
 * QCollidable.java
 * QCollidable class definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.geom.Polygon;
import chairosoft.ui.geom.FloatPoint2D;
import chairosoft.ui.geom.IntPoint2D;

import chairosoft.ui.graphics.DrawingContext;

import java.io.*; 
import java.util.*;

public class QCollidable extends QDrawable
{
	//
	// Static Variables 
	// 
	
	protected static boolean showDebug = false;
	
	
	//
	// Instance Variables 
	// 
	
	protected Set<Float>      slopes        = new HashSet<>();
	protected QCollidable     lastCollision = null;
	
	
	// 
	// Constructors
	// 
	
	protected QCollidable() { }
		
	protected QCollidable(int x, int y, int width, int height)
	{
		this.addPoint(x,         y);
		this.addPoint(x + width, y);
		this.addPoint(x + width, y + height);
		this.addPoint(x,         y + height);
	}
	
	
	// 
	// Static Methods 
	// 
	
	public static void startDebug() { showDebug = true; }
	public static void stopDebug() { showDebug = false; }
	
	
	// 
	// Instance Methods 
	// 
	
	// Accessors
	
	public FloatPoint2D getFirstVertex() 
	{
		if (this.points.isEmpty()) { return null; }
		return this.points.get(0).asFloatPoint2D();
	}
	
	public IntPoint2D getIntPosition() 
	{
		FloatPoint2D p = this.getFirstVertex();
		return new IntPoint2D(Math.round(p.x), Math.round(p.y));
	}
    
    public FloatPoint2D getCenterPosition()
    {
        FloatPoint2D result = new FloatPoint2D(0, 0);
        for (FloatPoint2D p : this.points) { result.x += p.x; result.y += p.y; }
        result.x /= this.points.size();
        result.y /= this.points.size();
        return result;
    }
    
    public IntPoint2D getIntCenterPosition()
    {
		FloatPoint2D p = this.getCenterPosition();
		return new IntPoint2D(Math.round(p.x), Math.round(p.y));
    }
	
	public String pointListFloat() 
	{
		String result = "[";
		for (FloatPoint2D p : this.points) { result += String.format("(%10f, %10f)", p.x, p.y); }
		result += "]";
		return result;
	}
	
	public String pointListInt() 
	{
		String result = "[";
        for (FloatPoint2D p : this.points) { result += String.format("(%10s, %10s)", Math.round(p.x), Math.round(p.y)); }
		result += "]";
		return result;
	}
	
	public String slopeList()
	{
		String result = "{";
		for (Float m : this.slopes) { result += String.format("%10f,", m); }
		result += "}";
		return result;
	}
	
	
	// Intraclass Mutators
	
	protected void recalculateSlopes() 
	{
		this.slopes.clear();
		if (this.points.size() < 2) { return; }
		
		for (int i = 0; i < this.points.size(); ++i)
		{
			FloatPoint2D pi = this.points.get(i);
			FloatPoint2D pj = this.points.get((i + 1) % this.points.size());
			
			float dx = pj.x - pi.x;
			float dy = pj.y - pi.y;
			
			// use the slope normal to each polygon edge, (-dx/dy), for projections
			float m = ((dy == 0.0f) ? Float.POSITIVE_INFINITY : (-dx / dy));
			if (m == -0.0f) { m = 0.0f; }
			this.slopes.add(m);
		}
	}
	
	@Override public void addPoint(int x, int y) { super.addPoint(x, y); this.recalculateSlopes(); }
	@Override public void reset() { super.reset(); this.slopes.clear(); }
    
	public void putFirstVertexAt(float x, float y) 
	{
		if (this.points.isEmpty()) { return; }
        FloatPoint2D p = this.points.get(0);
		float fdx = x - p.x;
		float fdy = y - p.y;
		this.translateFloat(fdx, fdy);
	}
	
	/** Clears all vertices and sets this {@code QCollidable}'s {@code image} to {@code null}. */
	@Override
	public void dispose()
	{
	    this.reset();
	    super.dispose();
	}
	
	
	// Extraclass Mutators
	
	public void drawVerticesToContext(DrawingContext ctx)
	{
		for (FloatPoint2D p : this.points)
		{
			ctx.drawOval(Math.round(p.x) - 1, Math.round(p.y) - 1, 2, 2);
		}
	}
	
	
	// QCollidable Accessors
	
	public boolean collidesWith(QCollidable that) 
	{
		return this.collidesWith_usingAxisOfSeparation(that);
	}
	
	protected boolean collidesWith_usingAxisOfSeparation(QCollidable that) 
	{
		boolean areColliding = true;
        
        try
		{
			if (this.points.isEmpty() || that.points.isEmpty()) { return false; }
			if (showDebug) { this.showDebugVertices(); that.showDebugVertices(); System.err.println(); }
			
			QCollidable[] collidablePair = new QCollidable[]{this, that};
			Set<Float> allSlopes = new HashSet<>();
			
			for (QCollidable c : collidablePair)
			{
				for (Float m : c.slopes) { allSlopes.add(m); }
			}
			
			// for each slope
				// for each polygon
					// for each vertex
						// project onto slope (onto the line [y = mx + 0], or the line [x = 0] for infinite slope)
					// find max and min vertex
				// determine via max/min vertices if overlap occurs
				// report false only if no overlap occurs
			
			for (float m : allSlopes)
			{
				boolean isInfinity = ((m == Float.POSITIVE_INFINITY) || (m == Float.NEGATIVE_INFINITY));
				boolean isZero = (m == 0.0f);
				//float m_squared = m * m; // comment out because dividing by (m^2 + 1) won't change the order
				
				float[] minima = new float[2];
				float[] maxima = new float[2];
				
				for (int i = 0; i < 2; ++i)
				{
					QCollidable c = collidablePair[i];
					
					List<Float> projectedVertices = new ArrayList<>(c.points.size());
					
					for (FloatPoint2D p : c.points)
					{
						// infinity case uses y-value of projected point
						// zero and general cases use x-value of projected point (general case was mathed on paper)
						if (isInfinity) { projectedVertices.add(p.y); }
						else if (isZero) { projectedVertices.add(p.x); }
						//else { projectedVertices.add( ((p.x + (m * p.y)) / (m_squared + 1)) ); }
						else { projectedVertices.add( (p.x + (m * p.y)) ); } // because dividing by (m^2 + 1) won't change the order
					}
					
					Collections.sort(projectedVertices);
					minima[i] = projectedVertices.get(0);
					maxima[i] = projectedVertices.get(projectedVertices.size() - 1);
				}
				
				if (minima[1] >= maxima[0] || minima[0] >= maxima[1])
				{
					areColliding = false;
					break;
				}
			}
            
            if (areColliding)
            {
                this.lastCollision = that;
                that.lastCollision = this;
            }
		}
		catch (Exception ex)
		{
			System.err.println("[collision separation axis]"); 
			System.err.print("--> this: ");
			this.showDebugVertices();
			System.err.print("--> that: ");
			that.showDebugVertices();
			ex.printStackTrace();
		}
        
        return areColliding;
	}
    
    public boolean containsPoint(FloatPoint2D p) { return this.containsPoint(p.x, p.y); }
    public boolean containsPoint(float px, float py)
    {
        boolean isContained = false;
        
        try
        {
			if (this.points.isEmpty()) { return false; }
            
            for (Float m : this.slopes)
            {
                
            }
        }
		catch (Exception ex)
		{
			System.err.println("[containsPoint]"); 
			System.err.print("--> this: ");
			this.showDebugVertices();
            System.err.println("--> point: (" + px + "," + py + ")");
			ex.printStackTrace();
		}
        
        return isContained;
    }
	
	
	protected void showDebugVertices()
	{
		if (this.points.isEmpty()) { return; }
		
		for (FloatPoint2D p : this.points)
		{
			System.err.print("(" + p.x + "," + p.y + ")");
		}
		System.err.println("");
	}
}