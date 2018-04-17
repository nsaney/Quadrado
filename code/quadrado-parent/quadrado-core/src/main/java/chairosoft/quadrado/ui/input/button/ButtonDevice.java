package chairosoft.quadrado.ui.input.button;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

/**
 * A device which provides button events.
 */
public interface ButtonDevice extends Closeable {
    
    ////// Constants //////
    public static final InfoDataKey<String> SERIAL_NUMBER = new InfoDataKey<>(String.class, "SERIAL_NUMBER");
    
    
    ////// Instance Methods - Abstract //////
    Info getButtonDeviceInfo();
    void open() throws IOException;
    boolean isOpen();
    void addButtonListener(ButtonListener listener);
    void removeButtonListener(ButtonListener listener);
    
    
    ////// Static Inner Classes //////
    class Info {
        //// Instance Fields ////
        public final String name;
        public final String description;
        protected final HashMap<String, Object> data = new HashMap<>();
        
        //// Constructor ////
        public Info(String _name, String _description) {
            this.name = _name;
            this.description = _description;
        }
        
        //// Instance Methods ////
        public <T> T getDataEntry(InfoDataKey<T> key) {
            return key.type.cast(this.data.get(key.name));
        }
        public <T> T putDataEntry(InfoDataKey<T> key, T value) {
            return key.type.cast(this.data.put(key.name, value));
        }
    }
    
    class InfoDataKey<T> {
        
        //// Instance Fields ////
        public final Class<? extends T> type;
        public final String name;
        
        //// Constructor ////
        public InfoDataKey(Class<? extends T> _type, String _name) {
            this.type = _type;
            this.name = _name;
        }
        
    }
    
}
