/* 
 * Nicholas Saney 
 * 
 * Created: April 28, 2015
 * 
 * QScrollAnimator.java
 * QScrollAnimator class definition
 * 
 */

package chairosoft.quadrado.element;

import java.util.ArrayDeque;

/**
 * A utility for animating a scrolling object (like QDialogBox or QTextListMenu).
 */
public class QScrollAnimator
{
    //
    // Instance Variables
    //
    
    protected int maxScrollHeight = 0;
    protected int scrollHeight = 0;
    protected final ArrayDeque<Integer> scrollQueue = new ArrayDeque<>();
    
    
    //
    // Constructor
    //
    
    public QScrollAnimator() { }
    
    
    //
    // Instance Methods 
    //
    
    public int getMaxScrollHeight() { return this.maxScrollHeight; }
    public void setMaxScrollHeight(int msh) { this.maxScrollHeight = msh; }
    
    public int getScrollHeight() { return this.scrollHeight; }
    public int getLatestScrollHeight()
    {
        int result = this.scrollHeight;
        if (this.scrollQueue.size() > 0)
        {
            result = this.scrollQueue.peek();
        }
        return result;
    }
    
    public void moveScrollHeight(int dh, int clicks) { this.setScrollHeight(this.scrollHeight + dh, clicks); }
    public void setScrollHeight(int sh, int clicks)
    {
        if (clicks == 0) { return; }
        
        if (sh < 0) { sh = 0; }
        else if (sh > this.maxScrollHeight) { sh = this.maxScrollHeight; }
        
        int difference = sh - this.scrollHeight;
        if (difference != 0)
        {
            this.scrollHeight = sh;
            
            double dh = difference / (double)clicks;
            for (int i = clicks; i-->0; )
            {
                int nextScrollHeight = this.scrollHeight - (int)(i * dh);
                this.scrollQueue.offer(nextScrollHeight);
            }
        }
    }
    
    public Integer pollScrollQueue()
    {
        return this.scrollQueue.poll();
    }
    
    public void clearScrollQueue()
    {
        this.scrollQueue.clear();
    }
}