/* 
 * Nicholas Saney 
 * 
 * Created: December 17, 2012
 * 
 * QApplication.java
 * QApplication abstract class definition (was QPanel)
 * 
 */

package chairosoft.quadrado.ui.system;

import chairosoft.quadrado.ui.input.button.ButtonEvent;
import chairosoft.quadrado.ui.input.button.ButtonListener;
import chairosoft.quadrado.ui.input.pointer.PointerEvent;
import chairosoft.quadrado.ui.input.pointer.PointerListener;
import chairosoft.quadrado.ui.graphics.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * The base of a Quadrado application - each Quadrado game should 
 * implement the abstract methods of this class. The core game loop code 
 * for this class was heavily informed by Chapter 2 of the book 
 * <i>Killer Game Programming with Java</i> by Dr. Andrew Davison 
 * (Copyright 2005 O'Reilly Media, Inc., ISBN 0-596-00730-2).
 * 
 * See also <a target="_top" href="http://fivedots.coe.psu.ac.th/~ad/jg/">Davison's web page about Java game programming</a>.
 */
public abstract class QApplication implements Runnable, ButtonListener, PointerListener
{
    
    //
    // Constants
    //
    
    public abstract int getPanelWidth();
    public abstract int getPanelHeight();
    public abstract int getXScaling();
    public abstract int getYScaling();
    public final int getPanelHalfWidth() { return this.getPanelWidth() / 2; }
    public final int getPanelHalfHeight() { return this.getPanelHeight() / 2; }
    
    public static final int DEFAULT_TIME_TARGET_PERIOD_IN_NS = 10 * 1000 * 1000; // about 100 Hz
    public static final int SLEEPLESS_UPDATES_PER_YIELD = 16;
    public static final int MAX_FRAME_SKIPS = 4;
    
    public static final int       int10e3 = 1000;
    public static final int       int10e6 = 1000000;
    public static final int       int10e9 = 1000000000;
    public static final double double10e6 = 1000000.0;
    public static final double double10e9 = 1000000000.0;
    
    
    // 
    // Static Inner Enum 
    // 
    
    public static enum ButtonState
    {
        BUTTON_PRESSED, 
        BUTTON_HELD, 
        BUTTON_RELEASED, 
        BUTTON_NOTHELD
    }
    
    
    // 
    // Instance Variables 
    // 
    
    public final String title;
    
    protected Thread animator;
    
    protected long timeThreadStart;
    protected long framesElapsedTotal = 0;
    
    protected int timeTargetPeriodInNanoseconds = DEFAULT_TIME_TARGET_PERIOD_IN_NS;
    public void setTimeTargetPeriodInNanoSeconds(int ns) { this.timeTargetPeriodInNanoseconds = ns; }
    public void setFrameRateInHz(int frameRateHz) { this.setTimeTargetPeriodInNanoSeconds(int10e9 / frameRateHz); }
    
    protected volatile boolean isRunning = false;
    protected volatile boolean showDebug = false;
    protected volatile boolean showDebugInConsole = false;
    
    // initial capacity = 16, load factor = 0.75, and concurrencyLevel = 1
    protected final Map<ButtonEvent.Code, ButtonState> buttonStates = new ConcurrentHashMap<>(16, 0.75f, 1);
    protected final ConcurrentLinkedQueue<PointerEvent> pointerEventQueue = new ConcurrentLinkedQueue<>();
    
    protected int sleeplessUpdates = 0;
    
    // double-buffer variables
    protected DoubleBufferedUI dbui = null;
    public DoubleBufferedUI getDbui() { return this.dbui; }
    
    // input requirements
    protected boolean requireButtonDevice = false;
    public boolean getRequireButtonDevice() { return this.requireButtonDevice; }
    public void setRequireButtonDevice(boolean _useButtonListener) { this.requireButtonDevice = _useButtonListener; }
    
    protected boolean usePointerListener = false;
    public boolean getUsePointerListener() { return this.usePointerListener; }
    public void setUsePointerListener(boolean _usePointerListener) { this.usePointerListener = _usePointerListener; }
    
    
    // more vars in subclass ...
    
    
    
    // 
    // Constructor 
    // 
    
    
    public QApplication(String _title)
    {
        this.title = _title;
        this.dbui = UserInterfaceProvider.get().createDoubleBufferedUI(
            this.title, 
            this.getPanelWidth(), 
            this.getPanelHeight(), 
            this.getXScaling(), 
            this.getYScaling()
        );
        // game components, initialized in subclass constructor ...
    }
    
    
    
    // 
    // Instance Methods 
    //
    
    
    /* initialize and start the thread */
    public boolean gameStart()
    {
        boolean isFirstCall = this.dbui.start();
        if (isFirstCall)
        {
            // attach listeners to UI
            // TODO: change over pointer listener to pointer device
            if (this.usePointerListener) { this.dbui.setPointerListener(this); }
            
            // start game thread
            this.animator = new Thread(this);
            this.animator.start();
            this.timeThreadStart = System.nanoTime();
        }
        return isFirstCall;
    }
    
    /* stops execution */
    public void gameStop() { isRunning = false; }
    
    
    /* do game initializations */
    protected abstract void qGameInitialize();
    
    /* repeat: update, render, sleep */
    public final void run()
    {
        try 
        {
            this.doRun();
        }
        catch (Exception ex) 
        {
            System.err.print("[qapplication]"); 
            ex.printStackTrace();
        }
        LifecycleUtility.get().exitApplication(0);
    }
    
    private void doRun() throws Exception
    {
        // allow initializations
        this.qGameInitialize();
        
        long timePreUpdate = 0L;
        long timePostUpdatePreRender = 0L;
        long timePostRender = 0L;
        long timeUpdateLength = 0L;
        long timeRenderLength = 0L;
        long timeActualSleptLength = 0L;
        long timeSleepLengthTarget = 0L;
        long timeOverSleptLength = 0L;
        long timeUnderSleptLength = 0L;
        
        // painting (doesn't affect anything except gameRenderDebugStats
        long timePrePaint = 0L;
        long timePaintLength = 0L;
        
        dbui.checkForPause();
        timePreUpdate = System.nanoTime();
        
        this.isRunning = true;
        while (isRunning)
        {
            this.dbui.ensureInput(this);
            this.gameUpdate();
            
            timePostUpdatePreRender = System.nanoTime();
            timeUpdateLength = timePostUpdatePreRender - timePreUpdate;
            
            this.gameRender();
            this.gameRenderDebugStats(timeUpdateLength, timeRenderLength, timePaintLength, timeActualSleptLength);
            
            timePrePaint = System.nanoTime();
            this.paintScreen();
            // timePostPaint = System.nanoTime();
            
            timePostRender = System.nanoTime();
            timeRenderLength = timePostRender - timePostUpdatePreRender;
            timePaintLength = timePostRender - timePrePaint;
            
            timeSleepLengthTarget = this.timeTargetPeriodInNanoseconds - timeUpdateLength - timeRenderLength - timeOverSleptLength;
            
            if (timeSleepLengthTarget > 0)
            {
                this.gameSleepNanos(timeSleepLengthTarget);
                timeActualSleptLength = (System.nanoTime() - timePostRender);
                timeOverSleptLength = timeActualSleptLength - timeSleepLengthTarget;
            }
            else
            {    
                if ((++this.sleeplessUpdates) > SLEEPLESS_UPDATES_PER_YIELD)
                {
                    Thread.yield();
                    this.sleeplessUpdates = 0;
                }
                timeUnderSleptLength -= timeSleepLengthTarget; // timeSleepLengthTarget <= 0
                timeActualSleptLength = 0L;
                timeOverSleptLength = 0L;
            }
            
            ++this.framesElapsedTotal;
            dbui.checkForPause();
            timePreUpdate = System.nanoTime();
            
            for (int framesSkipped = 0; (timeUnderSleptLength > this.timeTargetPeriodInNanoseconds) && (framesSkipped < MAX_FRAME_SKIPS); ++framesSkipped)
            {
                timeUnderSleptLength -= this.timeTargetPeriodInNanoseconds;
                this.gameUpdate();
            }
            //end-for framesSkipped
        }
        //end-while isRunning
        
        // game finish
        this.dbui.close();
        this.qGameFinish();
    }
    
    /* do game finish */
    protected abstract void qGameFinish();
    
    
    /* sleep a bit */
    private void gameSleepNanos(long nanoseconds) { this.gameSleepMillis(nanoseconds / int10e6); }
    private void gameSleepMillis(long milliseconds)
    {
        try 
        {
            Thread.sleep(milliseconds);
            this.sleeplessUpdates = 0;
        } 
        catch (InterruptedException ex) { } 
    }
    
    
    /* do button handling */
    protected abstract void qButtonPressed(ButtonEvent.Code buttonCode);
    protected abstract void qButtonHeld(ButtonEvent.Code buttonCode);
    protected abstract void qButtonReleased(ButtonEvent.Code buttonCode);
    protected abstract void qButtonNotHeld(ButtonEvent.Code buttonCode);
    
    @Override 
    public final void buttonPressed(ButtonEvent e)
    {
        // eliminate repeated button presses
        ButtonState state = this.buttonStates.get(e.code);
        if (state != ButtonState.BUTTON_PRESSED && state != ButtonState.BUTTON_HELD)
        {
            this.buttonStates.put(e.code, ButtonState.BUTTON_PRESSED);
        }
    }
    
    @Override 
    public final void buttonReleased(ButtonEvent e)
    {
        // eliminate repeated button releases
        ButtonState state = this.buttonStates.get(e.code);
        if (state != ButtonState.BUTTON_RELEASED && state != ButtonState.BUTTON_NOTHELD)
        {
            this.buttonStates.put(e.code, ButtonState.BUTTON_RELEASED);
        }
    }
    
    /* do pointer handling */
    protected abstract void qPointerPressed(float x, float y);
    protected abstract void qPointerMoved(float x, float y);
    protected abstract void qPointerReleased(float x, float y);
    
    @Override public final void pointerPressed(PointerEvent e) { this.pointerEventQueue.offer(e); }
    @Override public final void pointerMoved(PointerEvent e) { this.pointerEventQueue.offer(e); }
    @Override public final void pointerReleased(PointerEvent e) { this.pointerEventQueue.offer(e); }
    
    
    /* do game update */
    protected abstract void qGameUpdateInit();
    protected abstract void qGameUpdate();
    
    /* update game status */
    private void gameUpdate()
    {
        this.qGameUpdateInit();
        
        //if (this.framesElapsedTotal % 25 == 0) { System.err.println(buttonState); }
        Set<ButtonEvent.Code> buttonCodes = this.buttonStates.keySet();
        for (ButtonEvent.Code buttonCode : buttonCodes)
        {
            switch (this.buttonStates.get(buttonCode))
            {
                case BUTTON_PRESSED  : this.qButtonPressed(buttonCode); this.buttonStates.put(buttonCode, ButtonState.BUTTON_HELD); break;
                case BUTTON_HELD     : this.qButtonHeld(buttonCode); break;
                case BUTTON_RELEASED : this.qButtonReleased(buttonCode); this.buttonStates.put(buttonCode, ButtonState.BUTTON_NOTHELD); break;
                case BUTTON_NOTHELD  : this.qButtonNotHeld(buttonCode); break;
            }
        }
        
        int pointerEventCount = pointerEventQueue.size(); // only process mouse events in the queue as of this call
        for (int i = 0; i < pointerEventCount; ++i)
        {
            PointerEvent e = pointerEventQueue.poll();
            if (e != null) 
            {
                switch (e.state)
                {
                    case PointerEvent.PRESSED: this.qPointerPressed(e.x, e.y); break;
                    case PointerEvent.MOVED: this.qPointerMoved(e.x, e.y); break;
                    case PointerEvent.RELEASED: this.qPointerReleased(e.x, e.y); break;
                }
            }
        }
        
        
        this.qGameUpdate();
    }
    
    
    /* do game render */
    protected abstract void qGameRender(DrawingContext ctx);
    
    /* render game to the buffer */
    private void gameRender() 
    {
        DrawingContext renderContext = this.dbui.getRenderContext();
        if (renderContext.isReady())
        {
            renderContext.setColor(Color.WHITE);
            renderContext.fillRect(0, 0, this.getPanelWidth(), this.getPanelHeight());
            
            // draw game elements ...
            this.qGameRender(renderContext);
        }
    }
    
    /* render debug stats to the buffer */
    private static final FontFace DEBUG_FONT_FACE = UserInterfaceProvider.get().createFontFace(FontFamily.MONOSPACED, FontStyle.PLAIN, 12);
    private void gameRenderDebugStats(long timeUpdateLength, long timeRenderLength, long timePaintLength, long timeActualSleptLength)
    {
        DrawingContext ctx = this.dbui.getRenderContext();
        if (this.showDebug && (ctx != null) && ctx.isReady())
        {
            ctx.withSettingsRestored(() -> {
                ctx.setColor(0xbfffffff);
                ctx.fillRect(0, 0, this.getPanelWidth(), 200);
                
                ctx.setColor(Color.BLACK);
                ctx.setFontFace(QApplication.DEBUG_FONT_FACE);
                
                long timeTotalLoopLength = timeUpdateLength + timeRenderLength + timeActualSleptLength;
                long timeActualRenderLength = timeRenderLength - timePaintLength;
                ctx.drawLinesOfText(new String[] {
                    QApplication.getNumericDebugMessage("Actual update (ms)", timeUpdateLength / (double10e6)),
                    QApplication.getNumericDebugMessage("Actual render (ms)", timeActualRenderLength / (double10e6)),
                    QApplication.getNumericDebugMessage("Actual paint  (ms)", timePaintLength / (double10e6)),
                    QApplication.getNumericDebugMessage("Actual sleep  (ms)", timeActualSleptLength / (double10e6)),
                    QApplication.getNumericDebugMessage("Actual total  (ms)", timeTotalLoopLength / (double10e6)),
                    QApplication.getNumericDebugMessage("Target total  (ms)", this.timeTargetPeriodInNanoseconds / (double10e6)),
                    QApplication.getNumericDebugMessage("Target rate   (Hz)", (double10e9) / this.timeTargetPeriodInNanoseconds, 10),
                    QApplication.getNumericDebugMessage("Actual rate   (Hz)", (double10e9) / timeTotalLoopLength, 10),
                    QApplication.getNumericDebugMessage("Time elapsed   (s)", (System.nanoTime() - timeThreadStart) / (double10e9), 0)
                }, 20, 0);
            });
        }
    }
    
    /* get debug message for display */
    private static String getNumericDebugMessage(String title, double number)
    {
        return QApplication.getNumericDebugMessage(title, number, 1);
    }
    private static String getNumericDebugMessage(String title, double number, int dotDivisor)
    {
        String result = String.format("%15s %8.4f", title + ":", number);
        
        if (dotDivisor > 0)
        {
            String dots = "";
            int truncatedValue = (int)(number / dotDivisor);
            for (int i = 0; i < truncatedValue; ++i) { dots += "."; }
            result += String.format("[%-25s]", dots);
        }
        return result;
    }
    
    // /* display debug info on screen or console */
    // private void gameShowDebug(DrawingContext ctx, int x, int y, String title, double number)
    // {
        // this.gameShowDebug(ctx, x, y, title, number, 1);
    // }
    // private void gameShowDebug(DrawingContext ctx, int x, int y, String title, double number, int dotDivisor)
    // {
        // String message = QApplication.getNumericDebugMessage(title, number, dotDivisor);
        // ctx.drawString(message, x, y); 
    // }
    
    
    /* paint screen */
    private void paintScreen()
    {
        this.dbui.paintScreen();
    }
}