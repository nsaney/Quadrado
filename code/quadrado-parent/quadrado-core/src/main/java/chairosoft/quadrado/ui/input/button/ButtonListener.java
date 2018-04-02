/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * ButtonListener.java
 * ButtonListener interface definition
 * 
 */

package chairosoft.quadrado.ui.input.button;


public interface ButtonListener
{
    void buttonPressed(ButtonEvent e);
    void buttonReleased(ButtonEvent e);
}