/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DesktopDoubleBufferedUI.java
 * DesktopDoubleBufferedUI class definition
 * 
 */

package chairosoft.quadrado.desktop.system;


import chairosoft.quadrado.desktop.event.DesktopButtonAdapter;
import chairosoft.quadrado.desktop.event.DesktopPointerAdapter;
import chairosoft.quadrado.ui.system.DoubleBufferedUI;
import chairosoft.quadrado.ui.event.ButtonListener;
import chairosoft.quadrado.ui.event.PointerListener;

import chairosoft.quadrado.desktop.graphics.DesktopDrawingContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.*;

public class DesktopDoubleBufferedUI extends DoubleBufferedUI {
    
    ////// Instance Fields //////
    protected Image dbImage = null;
    public final Object pauseLock = new Object();
    public volatile boolean isPaused = false;
    
    
    ////// Instance Properties //////
    protected JPanel panel = null;
    public JPanel getPanel() {
        return this.panel;
    }
    
    protected DesktopButtonAdapter desktopButtonAdapter = null;
    @Override
    public void setButtonListener(ButtonListener _buttonListener) {
        super.setButtonListener(_buttonListener);
        
        // remove current adapter
        this.panel.removeKeyListener(this.desktopButtonAdapter);
        
        // add new adapter
        if (this.buttonListener != null) {
            this.desktopButtonAdapter = new DesktopButtonAdapter(this.buttonListener);
            this.panel.addKeyListener(this.desktopButtonAdapter);
        }
    }
    
    protected DesktopPointerAdapter desktopPointerAdapter = null;
    @Override
    public void setPointerListener(PointerListener _pointerListener) {
        super.setPointerListener(_pointerListener);
        
        // remove current adapter
        this.panel.removeMouseListener(this.desktopPointerAdapter);
        this.panel.removeMouseMotionListener(this.desktopPointerAdapter);
        
        // add new adapter
        if (this.pointerListener != null) {
            this.desktopPointerAdapter = new DesktopPointerAdapter(this.pointerListener);
            this.panel.addMouseListener(this.desktopPointerAdapter);
            this.panel.addMouseMotionListener(this.desktopPointerAdapter);
        }
    }
    
    
    ////// Constructor //////
    public DesktopDoubleBufferedUI(String _title, int _width, int _height, int _xScaling, int _yScaling) {
        super(_title, _width, _height, _xScaling, _yScaling);
    }
    
    
    ////// Instance Methods //////
    @Override
    protected void doStart() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(this.title);
        
        this.panel = new JPanel();
        this.panel.setBackground(Color.BLACK);
        this.panel.setPreferredSize(new Dimension(this.width * this.xScaling, this.height * this.yScaling));
        this.panel.setDoubleBuffered(false); // because this takes care of it
        this.panel.setFocusable(true);
        this.panel.requestFocus(); // receiving key events
        
        frame.getContentPane().add(this.panel, BorderLayout.CENTER);
        
        frame.setResizable(false); // set this before pack() to avoid change in window size
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowFocusListener(new DesktopPausingWindowFocusListener(this));
    }
    
    @Override
    protected void ensureRenderContext() {
        if (this.dbImage == null) {
            this.dbImage = this.panel.createImage(this.width, this.height);
            if (this.dbImage == null) {
                System.err.println("Error in ensureRenderContext(): Unable to create Image from JPanel.");
                return;
            }
        }
        
        if (this.renderContext == null) {
            Graphics dbGraphics = this.dbImage.getGraphics();
            this.renderContext = new DesktopDrawingContext(dbGraphics);
        }
    }
    
    @Override
    public void paintScreen() {
        try (DesktopDrawingContext context = new DesktopDrawingContext(this.panel.getGraphics())) {
            Graphics2D gfx = context.graphics;
            if ((gfx != null) && (this.dbImage != null)) {
                gfx.scale(this.xScaling, this.yScaling);
                gfx.drawImage(this.dbImage, 0, 0, null);
            }
            else {
                String message = String.format(
                    "Error in paintScreen(): gfx = %1$s, this.dbImage = %2$s",
                    gfx,
                    this.dbImage
                );
                System.err.println(message);
            }
            
            Toolkit.getDefaultToolkit().sync(); // sync the display on some systems
        }
        catch (Exception ex) {
            System.err.println("DrawingContext error: " + ex);
        }
    }
    
    @Override
    public void checkForPause() {
        synchronized (this.pauseLock) {
            while (this.isPaused) {
                try {
                    this.pauseLock.wait();
                }
                catch (InterruptedException ex) {
                    // nothing here right now
                }
            }
        }
    }
}