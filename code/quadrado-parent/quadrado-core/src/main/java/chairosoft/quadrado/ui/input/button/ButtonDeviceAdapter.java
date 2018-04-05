package chairosoft.quadrado.ui.input.button;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public abstract class ButtonDeviceAdapter implements ButtonDevice {
    
    ////// Instance Fields //////
    public final Info info;
    private final Set<ButtonListener> buttonListeners = new CopyOnWriteArraySet<>();
    
    
    ////// Constructor //////
    protected ButtonDeviceAdapter(Info _info) {
        this.info = _info;
    }
    
    
    ////// Instance Methods //////
    @Override
    public Info getButtonDeviceInfo() {
        return this.info;
    }
    
    @Override
    public void addButtonListener(ButtonListener listener) {
        this.buttonListeners.add(listener);
    }
    
    @Override
    public void removeButtonListener(ButtonListener listener) {
        this.buttonListeners.remove(listener);
    }
    
    protected void forEachButtonListener(Consumer<ButtonListener> action) {
        this.buttonListeners.forEach(action);
    }
    
    @Override
    public String toString() {
        return String.format(
            "%s|name=%s|open=%s",
            this.getClass(),
            this.info.name,
            this.isOpen()
        );
    }
}
