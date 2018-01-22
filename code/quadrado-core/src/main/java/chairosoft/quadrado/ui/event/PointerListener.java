/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * PointerListener.java
 * PointerListener interface definition
 * 
 */

package chairosoft.quadrado.ui.event;


public interface PointerListener
{
    void pointerPressed(PointerEvent e);
    void pointerMoved(PointerEvent e);
    void pointerReleased(PointerEvent e);
}