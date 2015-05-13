/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DoubleBufferedUI.java
 * DoubleBufferedUI class definition
 * 
 */

package chairosoft.quadrado.platform;

import chairosoft.quadrado.QApplication;

import chairosoft.dependency.Dependencies;
import chairosoft.ui.event.ButtonListener;
import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;


public abstract class DoubleBufferedUI
{
    protected ButtonListener buttonListener = null;
    protected String title = "";
    protected int width = -1;
    protected int height = -1;
    protected int xScaling = -1;
    protected int yScaling = -1;
    
    public static DoubleBufferedUI create(QApplication app)
    {
        DoubleBufferedUI result = Dependencies.getNew(DoubleBufferedUI.class);
        result.buttonListener = app;
        result.title = app.title;
        result.width = app.getPanelWidth();
        result.height = app.getPanelHeight();
        result.xScaling = app.getXScaling();
        result.yScaling = app.getYScaling();
        return result;
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
    
    // Overwrite this where necessary
    public void checkForPause() { }
}