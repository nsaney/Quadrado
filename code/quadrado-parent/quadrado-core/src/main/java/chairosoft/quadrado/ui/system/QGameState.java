package chairosoft.quadrado.ui.system;

import chairosoft.quadrado.ui.graphics.DrawingContext;
import chairosoft.quadrado.ui.input.button.ButtonEvent;

public abstract class QGameState {
    
    ////// Constructor //////
    protected QGameState() {
        // nothing here right now
    }
    
    
    ////// Instance Methods - Input //////
    public void buttonPressed(ButtonEvent.Code buttonCode) {
        // nothing here right now
    }
    public void buttonHeld(ButtonEvent.Code buttonCode) {
        // nothing here right now
    }
    public void buttonReleased(ButtonEvent.Code buttonCode) {
        // nothing here right now
    }
    public void buttonNotHeld(ButtonEvent.Code buttonCode) {
        // nothing here right now
    }
    public void pointerPressed(float x, float y) {
        // nothing here right now
    }
    public void pointerMoved(float x, float y) {
        // nothing here right now
    }
    public void pointerReleased(float x, float y) {
        // nothing here right now
    }
    
    
    ////// Instance Methods - Update //////
    public void updateInit() {
        // nothing here right now
    }
    public void update() {
        // nothing here right now
    }
    
    
    ////// Instance Methods - Output //////
    public abstract void render(DrawingContext ctx);
    
    // TODO: "write next audio?"
}
