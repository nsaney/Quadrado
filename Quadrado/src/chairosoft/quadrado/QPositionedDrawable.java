/* 
 * Nicholas Saney 
 * 
 * Created: April 24, 2015
 * Modified: April 24, 2015
 * 
 * QPositionedDrawable.java
 * QPositionedDrawable interface definition
 * 
 */

package chairosoft.quadrado;

import chairosoft.ui.graphics.DrawingContext;

public interface QPositionedDrawable
{
    void drawToContextAtOwnPosition(DrawingContext ctx);
}