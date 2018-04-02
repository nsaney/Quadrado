/* 
 * Nicholas Saney 
 * 
 * Created: November 16, 2013
 * 
 * QCompassKeypad.java
 * QCompassKeypad class definition
 * 
 */

package chairosoft.quadrado.ui.input.direction;


public class CompassKeypad
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
    
    protected SingleAxis<CompassDirection> horizontalAxis = new SingleAxis<>(CompassDirection.WEST, CompassDirection.CENTER, CompassDirection.EAST);
    protected SingleAxis<CompassDirection> verticalAxis = new SingleAxis<>(CompassDirection.NORTH, CompassDirection.CENTER, CompassDirection.SOUTH);
    
    
    //
    // Constructor
    //
    
    public CompassKeypad() { }
    
    
    //
    // Instance Accessors
    //
    
    public CompassDirection getDirection()
    {
        CompassDirection horizontalDirection = horizontalAxis.getAxisPosition();
        CompassDirection verticalDirection = verticalAxis.getAxisPosition();
        return CompassDirection.combineDirections(horizontalDirection, verticalDirection);
    }
    
    
    //
    // Instance Mutators
    //
    
    public void activateValue(CompassDirection direction)
    {
        // ignores non-cardinal directions
        if (direction.IS_HORIZONTAL) { horizontalAxis.activateValue(direction); }
        if (direction.IS_VERTICAL) { verticalAxis.activateValue(direction); }
    }
    
    public void deactivateValue(CompassDirection direction)
    {
        // ignores non-cardinal directions
        if (direction.IS_HORIZONTAL) { horizontalAxis.deactivateValue(direction); }
        if (direction.IS_VERTICAL) { verticalAxis.deactivateValue(direction); }
    }
}