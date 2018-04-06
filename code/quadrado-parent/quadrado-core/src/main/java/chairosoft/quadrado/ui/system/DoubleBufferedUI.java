/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DoubleBufferedUI.java
 * DoubleBufferedUI class definition
 * 
 */

package chairosoft.quadrado.ui.system;

import chairosoft.quadrado.ui.graphics.DrawingContext;
import chairosoft.quadrado.ui.input.button.ButtonDevice;
import chairosoft.quadrado.ui.input.pointer.PointerListener;

import java.io.Closeable;
import java.io.IOException;

public abstract class DoubleBufferedUI implements Closeable {
    
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
    protected ButtonDevice buttonDevice = null;
    
    
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
    protected abstract ButtonDevice chooseButtonDevice();
    
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
    
    public final void ensureInput(QApplication qApplication) throws IOException {
        if (this.buttonDevice == null && qApplication.getRequireButtonDevice()) {
            this.buttonDevice = this.chooseButtonDevice();
            if (this.buttonDevice == null) {
                throw new IllegalStateException("Could not ensure a button device for input.");
            }
            this.buttonDevice.addButtonListener(qApplication);
            this.buttonDevice.open();
        }
    }
    
    public final void close() throws IOException {
        if (this.buttonDevice != null) {
            this.buttonDevice.close();
        }
    }
    
}