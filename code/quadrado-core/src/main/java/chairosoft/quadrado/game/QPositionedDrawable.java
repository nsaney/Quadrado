/* 
 * Nicholas Saney 
 * 
 * Created: April 24, 2015
 * 
 * QPositionedDrawable.java
 * QPositionedDrawable interface definition
 * 
 */

package chairosoft.quadrado.game;

import chairosoft.quadrado.ui.graphics.DrawingContext;

public interface QPositionedDrawable
{
    void drawToContextAtOwnPosition(DrawingContext ctx);
}