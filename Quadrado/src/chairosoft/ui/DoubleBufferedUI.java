/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DoubleBufferedUI.java
 * DoubleBufferedUI class definition
 * 
 */

package chairosoft.ui;

import chairosoft.dependency.Dependencies;
import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;
import chairosoft.ui.event.ButtonListener;

public abstract class DoubleBufferedUI
{
    protected String title = "";
    protected int width = -1;
    protected int height = -1;
    protected int xScaling = -1;
    protected int yScaling = -1;
    
    protected ButtonListener buttonListener = null;
    
    
    public static DoubleBufferedUI create(String _title, int _width, int _height, int _xScaling, int _yScaling)
    {
        DoubleBufferedUI result = Dependencies.getNew(DoubleBufferedUI.class);
        result.title = _title;
        result.width = _width;
        result.height = _height;
        result.xScaling = _xScaling;
        result.yScaling = _yScaling;
        return result;
    }
    
    public void setButtonListener(ButtonListener _buttonListener)
    {
        this.buttonListener = _buttonListener;
    }
    
    private boolean isStarted = false;
    protected abstract void doStart();
    public final boolean start()
    {
        boolean isFirstCall = !this.isStarted;
        this.isStarted = true;
        if (isFirstCall)
        {
            this.doStart();
        }
        return isFirstCall;
    }
    
    protected DrawingContext renderContext = null;
    protected abstract void ensureRenderContext();
    public final DrawingContext getRenderContext()
    {
        this.ensureRenderContext();
        return this.renderContext;
    }
    
    public abstract void paintScreen();
    
    /**
     * This method should be overriden in a subclass 
     * to check lock objects (etc) for a pause state.
     */
    public abstract void checkForPause();
}