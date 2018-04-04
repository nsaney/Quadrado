package chairosoft.quadrado.ui.input.button;

import java.io.Closeable;

/**
 * A device which provides button events.
 */
public interface ButtonDevice extends Closeable {
    
    ////// Instance Methods //////
    Info getButtonDeviceInfo();
    void open();
    boolean isOpen();
    void addButtonListener(ButtonListener listener);
    void removeButtonListener(ButtonListener listener);
    
    
    ////// Static Inner Classes //////
    class Info {
        //// Instance Fields ////
        public final long id;
        public final String name;
        public final String description;
        
        //// Constructor ////
        public Info(long _id, String _name, String _description) {
            this.id = _id;
            this.name = _name;
            this.description = _description;
        }
    }
    
}
