/* 
 * Nicholas Saney 
 * 
 * Created: November 16, 2013
 * 
 * QCompassKeypad.java
 * QCompassKeypad class definition
 * 
 */

package chairosoft.quadrado.game;

import static chairosoft.quadrado.game.QCompassDirection.*;


import java.io.*; 
import java.util.*;


public class QCompassKeypad
{
    //
    // Static Inner Class
    //
    
    public static class SingleAxis<T>
    {
        // Instance Constants
        public final T negativeValue;
        public final T zeroValue;
        public final T positiveValue;
        
        // Instance Variables
        protected boolean negativeIsActive = false;
        protected boolean positiveIsActive = false;
        protected boolean positiveIsMostRecent = false;
        
        // Constructor
        public SingleAxis(T _negativeValue, T _zeroValue, T _positiveValue)
        {
            this.negativeValue = _negativeValue;
            this.zeroValue     = _zeroValue;
            this.positiveValue = _positiveValue;
        }
        
        // Instance Accessors
        public T getAxisPosition()
        {
            T result = this.zeroValue;
            
            if (this.negativeIsActive || this.positiveIsActive)
            {
                if (!this.positiveIsActive) { result = this.negativeValue; }
                else if (!this.negativeIsActive) { result = this.positiveValue; }
                else { result = this.positiveIsMostRecent ? this.positiveValue : this.negativeValue; }
            }
            
            return result;
        }
        
        // Instance Mutators
        public void activateValue(T value)
        {
            if (value.equals(this.negativeValue))
            {
                this.positiveIsMostRecent = false;
                this.negativeIsActive = true;
            }
            else if (value.equals(this.positiveValue))
            {
                this.positiveIsMostRecent = true;
                this.positiveIsActive = true;
            }
        }
        
        public void deactivateValue(T value)
        {
            if (value.equals(this.negativeValue)) { this.negativeIsActive = false; }
            else if (value.equals(this.positiveValue)) { this.positiveIsActive = false; }
        }
    }
    
    
    //
    // Instance Variables
    //
    
    protected SingleAxis<QCompassDirection> horizontalAxis = new SingleAxis<>(WEST, CENTER, EAST);
    protected SingleAxis<QCompassDirection> verticalAxis = new SingleAxis<>(NORTH, CENTER, SOUTH);
    
    
    //
    // Constructor
    //
    
    public QCompassKeypad() { }
    
    
    //
    // Instance Accessors
    //
    
    public QCompassDirection getDirection()
    {
        QCompassDirection horizontalDirection = horizontalAxis.getAxisPosition();
        QCompassDirection verticalDirection = verticalAxis.getAxisPosition();
        return QCompassDirection.combineDirections(horizontalDirection, verticalDirection);
    }
    
    
    //
    // Instance Mutators
    //
    
    public void activateValue(QCompassDirection direction)
    {
        // ignores non-cardinal directions
        if (direction.IS_HORIZONTAL) { horizontalAxis.activateValue(direction); }
        if (direction.IS_VERTICAL) { verticalAxis.activateValue(direction); }
    }
    
    public void deactivateValue(QCompassDirection direction)
    {
        // ignores non-cardinal directions
        if (direction.IS_HORIZONTAL) { horizontalAxis.deactivateValue(direction); }
        if (direction.IS_VERTICAL) { verticalAxis.deactivateValue(direction); }
    }
}