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

public interface ButtonListener {
    void buttonPressed(ButtonEvent e);
    void buttonReleased(ButtonEvent e);
    default void handleButtonEvent(ButtonEvent.Type type, ButtonEvent e) {
        if (type == ButtonEvent.Type.PRESSED) {
            this.buttonPressed(e);
        }
        else if (type == ButtonEvent.Type.RELEASED) {
            this.buttonReleased(e);
        }
        else {
            throw new UnsupportedOperationException("Could not handle button event type: " + type);
        }
    }
}
