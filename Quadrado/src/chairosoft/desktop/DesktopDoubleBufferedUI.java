/* 
 * Nicholas Saney 
 * 
 * Created: January 19, 2015
 * 
 * DesktopDoubleBufferedUI.java
 * DesktopDoubleBufferedUI class definition
 * 
 */

package chairosoft.desktop;


import chairosoft.ui.DoubleBufferedUI;
import chairosoft.ui.event.ButtonListener;
import chairosoft.ui.event.ButtonEvent;
import chairosoft.ui.event.ButtonSource;
import chairosoft.ui.event.PointerListener;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;
import chairosoft.desktop.graphics.DesktopDrawingImage;
import chairosoft.desktop.graphics.DesktopDrawingContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DesktopDoubleBufferedUI extends DoubleBufferedUI
{
    protected Image dbImage = null;
    protected JPanel panel = null;
    public JPanel getPanel() { return this.panel; }
    
    protected DesktopButtonAdapter desktopButtonAdapter = null;
    protected DesktopPointerAdapter desktopPointerAdapter = null;
    
    @Override 
    protected void doStart()
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    }
    
    @Override 
    protected void ensureRenderContext()
    {
        if (this.dbImage == null)
        {
            this.dbImage = this.panel.createImage(this.width, this.height);
            if (this.dbImage == null) 
            {
                System.err.println("Error in ensureRenderContext(): Unable to create Image from JPanel."); 
                return; 
            }
        }
        
        if (this.renderContext == null)
        {
            Graphics dbGraphics = this.dbImage.getGraphics();
            this.renderContext = new DesktopDrawingContext(dbGraphics);
        }
    }
    
    @Override 
    public void paintScreen()
    {
        try (DesktopDrawingContext context = new DesktopDrawingContext(this.panel.getGraphics()))
        {
            Graphics2D gfx = context.graphics;
            if ((gfx != null) && (this.dbImage != null)) 
            {
                gfx.scale(this.xScaling, this.yScaling);
                gfx.drawImage(this.dbImage, 0, 0, null); 
            }
            else 
            {
                String message = String.format("Error in paintScreen(): gfx = %1$s, this.dbImage = %2$s", gfx, this.dbImage);
                System.err.println(message);
            }
            
            Toolkit.getDefaultToolkit().sync(); // sync the display on some systems
        }
        catch (Exception ex) 
        {
            System.err.println("DrawingContext error: " + ex);
        }
    }
    
    
    /* watch for button events */
    @Override
    public void setButtonListener(ButtonListener _buttonListener)
    {
        super.setButtonListener(_buttonListener);
        
        // remove current adapter
        this.panel.removeKeyListener(this.desktopButtonAdapter);
        
        // add new adapter
        this.desktopButtonAdapter = new DesktopButtonAdapter(this.buttonListener);
        this.panel.addKeyListener(this.desktopButtonAdapter);
    }
    
    /* watch for pointer events */
    @Override
    public void setPointerListener(PointerListener _pointerListener)
    {
        super.setPointerListener(_pointerListener);
        
        // remove current adapter
        this.panel.removeMouseListener(this.desktopPointerAdapter);
        this.panel.removeMouseMotionListener(this.desktopPointerAdapter);
        
        // add new adapter
        this.desktopPointerAdapter = new DesktopPointerAdapter(this.pointerListener);
        this.panel.addMouseListener(this.desktopPointerAdapter);
        this.panel.addMouseMotionListener(this.desktopPointerAdapter);
    }
    
    @Override
    public void checkForPause()
    {
        // currently does nothing
    }
}