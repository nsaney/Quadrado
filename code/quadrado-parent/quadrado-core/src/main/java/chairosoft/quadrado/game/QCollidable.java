/* 
 * Nicholas Saney 
 * 
 * Created: January 18, 2013
 * 
 * QCollidable.java
 * QCollidable class definition
 * 
 */

package chairosoft.quadrado.game;

import chairosoft.quadrado.ui.geom.FloatPoint2D;
import chairosoft.quadrado.ui.geom.IntPoint2D;

import chairosoft.quadrado.ui.graphics.DrawingContext;

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
    
    public QCollidable() { }
        
    public QCollidable(int x, int y, int width, int height)
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
    @Override public void addPoint(float x, float y) { super.addPoint(x, y); this.recalculateSlopes(); }
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
    
    /**
     * Determines if this object collides with the given QCollidable 
     * using the axis of separation algorithm.
     * @param that the other QCollidable to check for collision with
     * @return true if both QCollidable objects collide; false otherwise
     */
    public boolean collidesWith(QCollidable that) 
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
    
    /**
     * Calculates "isLeft" for the winding-number algorithm from
     * <a href="http://geomalgorithms.com/a03-_inclusion.html">
     * http://geomalgorithms.com/a03-_inclusion.html
     * </a>.
     * @param p0 one point of a line
     * @param p1 another point of a line
     * @param p2_x the x-value of the point to test if it "isLeft" of the line defined by p0 and p1
     * @param p2_y the y-value of the point to test if it "isLeft" of the line defined by p0 and p1
     * @return &gt;0 for P2 left of the line through P0 and P1;
     *         =0 for P2 on the line;
     *         &lt;0 for P2 right of the line
     */
    private static float wn_isLeft(FloatPoint2D p0, FloatPoint2D p1, float p2_x, float p2_y)
    {
        float dx1 = p1.x - p0.x;
        float dy1 = p1.y - p0.y;
        float dx2 = p2_x - p0.x;
        float dy2 = p2_y - p0.y;
        return (dx1 * dy2) - (dx2 * dy1);
    }
    
    public boolean containsPoint(FloatPoint2D p) { return this.containsPoint(p.x, p.y); }
    
    /**
     * Determines if the given point is inside this QCollidable using 
     * the winding-number algorithm from 
     * <a href="http://geomalgorithms.com/a03-_inclusion.html">
     * http://geomalgorithms.com/a03-_inclusion.html
     * </a>.
     * @param px the x-value of the point to check
     * @param py the y-value of the point to check
     * @return true if the given point is inside this QCollidable; false otherwise
     */
    public boolean containsPoint(float px, float py) 
    {
        boolean isContained = false;
        
        try
        {
            if (this.points.isEmpty()) { return false; }
            
            int wn = 0; // the winding number counter

            // loop through all edges of the polygon
            int n = this.points.size();
            for (int i = 0, j = 1; i < n; ++i, ++j)        // edge from vi to vj
            {
                FloatPoint2D vi = this.points.get(i);
                FloatPoint2D vj = this.points.get(j == n ? 0 : j);
                if (vi.y <= py)                            // start y <= p.y
                {
                    if (vj.y > py)                         // an upward crossing
                    {
                        if (wn_isLeft(vi, vj, px, py) > 0) // p left of edge
                        {
                            ++wn;                          // have a valid up intersect
                        }
                    }
                }
                else                                       // start y > p.y (no test needed) 
                {
                    if (vj.y <= py)                        // a downward crossing
                    {
                        if (wn_isLeft(vi, vj, px, py) < 0) // p right of edge
                        {
                            --wn;                          // have a valid down intersect
                        }
                    }
                }
            }
            
            isContained = (wn != 0);
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
    
    
    //
    // Integer-Offset Collidable
    //
    
    public static class IntOffset extends QCollidable
    {
        // Instance Fields
        public final int dx;
        public final int dy;
        
        // Constructor
        public IntOffset(int _dx, int _dy)
        {
            this.dx = _dx;
            this.dy = _dy;
        }
        
        // Instance Methods
        @Override public void addPoint(int x, int y) { super.addPoint(x + dx, y + dy); }
        @Override public void addPoint(float x, float y) { super.addPoint(x + dx, y + dy); }
        @Override public void putFirstVertexAt(float x, float y) { super.putFirstVertexAt(x + dx, y + dy); }
    }
}