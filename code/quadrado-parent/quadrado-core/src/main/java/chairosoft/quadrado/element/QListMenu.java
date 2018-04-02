/* 
 * Nicholas Saney 
 * 
 * Created: April 25, 2015
 * 
 * QListMenu.java
 * QListMenu abstract class definition
 * 
 */

package chairosoft.quadrado.element;

// import chairosoft.quadrado.ui.graphics.Color;
// import chairosoft.quadrado.ui.graphics.DrawingContext;
// import chairosoft.quadrado.ui.graphics.DrawingImage;
// import chairosoft.quadrado.ui.graphics.FontFace;
// import chairosoft.quadrado.ui.graphics.FontLayout;
// import chairosoft.quadrado.ui.graphics.WrappedText;

// import java.util.ArrayDeque;


/**
 * A selectable menu whose menu items are shown in a list.
 */
public abstract class QListMenu extends QSelectableMenu
{
    //
    // Instance Variables
    //
    
    private int selectionIndex = 0;
    
    
    //
    // Constructor
    //
    
    public QListMenu(String _title, MenuItem... _menuItems)
    {
        super(_title, _menuItems);
    }
    
    
    //
    // Instance Methods 
    //
    
    @Override
    public void selectDefaultItem()
    {
        super.selectDefaultItem();
        this.setSelectionIndex(0);
    }
    
    public void selectPreviousItem() { this.moveSelectionBy(-1); }
    public void selectNextItem() { this.moveSelectionBy(+1); }
    public void moveSelectionBy(int di) { this.setSelectionIndex(this.selectionIndex + di); }
    public int getCorrectedIndex(int index)
    {
        if (index < 0) { index = 0; }
        else if (index >= this.readonlyMenuItems.size()) { index = this.readonlyMenuItems.size() - 1; }
        return index;
    }
    
    public int getSelectionIndex() { return this.selectionIndex; }
    public void setSelectionIndex(int index)
    {
        index = this.getCorrectedIndex(index);
        if (index != this.selectionIndex)
        {
            int oldIndex = this.selectionIndex;
            this.selectionIndex = index;
            this.selectedItem = this.readonlyMenuItems.get(this.selectionIndex);
            this.onSelectionChange(this.selectionIndex, oldIndex);
        }
    }
    protected void onSelectionChange(int updatedIndex, int oldIndex) { }
}