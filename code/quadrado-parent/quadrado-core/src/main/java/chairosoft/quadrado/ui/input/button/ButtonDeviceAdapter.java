package chairosoft.quadrado.ui.input.button;

public abstract class ButtonDeviceAdapter implements ButtonDevice {
    
    ////// Instance Fields //////
    public final Info info;
    
    
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
    public String toString() {
        return String.format(
            "%s|name=%s|open=%s",
            this.getClass(),
            this.info.name,
            this.isOpen()
        );
    }
}
