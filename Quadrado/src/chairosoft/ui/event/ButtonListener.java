/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * ButtonListener.java
 * ButtonListener interface definition
 * 
 */

package chairosoft.ui.event;


public interface ButtonListener
{
    void buttonPressed(ButtonEvent e);
    void buttonReleased(ButtonEvent e);
}