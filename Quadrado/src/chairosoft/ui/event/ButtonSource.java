/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * Modified: January 23, 2015
 * 
 * ButtonSource.java
 * ButtonSource class definition
 * 
 */

package chairosoft.ui.event;


public class ButtonSource
{
    private static int nextId = 0;
    public final int id;
    public final String description;
    public ButtonSource(String _description)
    {
        this.id = ButtonSource.nextId++;
        this.description = _description;
    }
    
    public static ButtonSource KEYBOARD = new ButtonSource("Keyboard");
}