/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DoubleBufferedUI.java
 * DoubleBufferedUI class definition
 * 
 */

package chairosoft.quadrado.ui;

import chairosoft.quadrado.ui.graphics.DrawingContext;
import chairosoft.quadrado.ui.event.ButtonListener;
import chairosoft.quadrado.ui.event.PointerListener;

public abstract class DoubleBufferedUI {
    
    ////// Instance Properties //////
    protected String title = "";
    public String getTitle() {
        return this.title;
    }
    
    protected int width = -1;
    public int getWidth() {
        return this.width;
    }
    
    protected int height = -1;
    public int getHeight() {
        return this.height;
    }
    
    protected int xScaling = -1;
    public int getXScaling() {
        return this.xScaling;
    }
    
    protected int yScaling = -1;
    public int getYScaling() {
        return this.yScaling;
    }
    
    protected ButtonListener buttonListener = null;
    public ButtonListener getButtonListener() {
        return this.buttonListener;
    }
    public void setButtonListener(ButtonListener _buttonListener) {
        this.buttonListener = _buttonListener;
    }
    
    protected PointerListener pointerListener = null;
    public PointerListener getPointerListener() {
        return this.pointerListener;
    }
    public void setPointerListener(PointerListener _pointerListener) {
        this.pointerListener = _pointerListener;
    }
    
    protected DrawingContext renderContext = null;
    public final DrawingContext getRenderContext() {
        this.ensureRenderContext();
        return this.renderContext;
    }
    
    
    ////// Instance Fields //////
    private boolean isStarted = false;
    
    
    ////// Constructor //////
    protected DoubleBufferedUI(String _title, int _width, int _height, int _xScaling, int _yScaling) {
        this.title = _title;
        this.width = _width;
        this.height = _height;
        this.xScaling = _xScaling;
        this.yScaling = _yScaling;
    }
    
    
    ////// Instance Methods - Abstract //////
    protected abstract void doStart();
    protected abstract void ensureRenderContext();
    public abstract void paintScreen();
    
    /**
     * This method should be overriden in order
     * to check lock objects (etc) for a pause state.
     */
    public abstract void checkForPause();
    
    
    ////// Instance Methods - Concrete //////
    public final boolean start() {
        boolean isFirstCall = !this.isStarted;
        this.isStarted = true;
        if (isFirstCall) {
            this.doStart();
        }
        return isFirstCall;
    }
    
}