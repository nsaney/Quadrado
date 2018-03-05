package chairosoft.quadrado.ui.event;

import java.io.Closeable;

/**
 * A device which provides button events.
 */
public interface ButtonDevice extends Closeable {
    
    long getId();
    
    
}
