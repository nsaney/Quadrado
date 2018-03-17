/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * ButtonListener.java
 * ButtonListener interface definition
 * 
 */

package chairosoft.quadrado.ui.input;


public interface ButtonListener
{
    void buttonPressed(ButtonEvent e);
    void buttonReleased(ButtonEvent e);
}