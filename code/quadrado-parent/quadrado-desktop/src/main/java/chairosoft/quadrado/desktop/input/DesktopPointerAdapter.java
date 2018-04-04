/* 
 * Nicholas Saney 
 * 
 * Created: January 20, 2015
 * 
 * DesktopPointerAdapter.java
 * DesktopPointerAdapter class definition
 * 
 */

package chairosoft.quadrado.desktop.input;


import chairosoft.quadrado.ui.input.pointer.PointerListener;
import chairosoft.quadrado.ui.input.pointer.PointerEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class DesktopPointerAdapter extends MouseAdapter
{
    private final PointerListener pointerListener;
    public DesktopPointerAdapter(PointerListener _pointerListener)
    {
        this.pointerListener = _pointerListener;
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        this.pointerListener.pointerPressed(new PointerEvent(PointerEvent.PRESSED, e.getX(), e.getY()));
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
        this.pointerListener.pointerMoved(new PointerEvent(PointerEvent.MOVED, e.getX(), e.getY()));
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        this.pointerListener.pointerReleased(new PointerEvent(PointerEvent.RELEASED, e.getX(), e.getY()));
    }
}