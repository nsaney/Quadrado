/* 
 * Nicholas Saney 
 * 
 * Created: April 24, 2015
 * 
 * QSelectableMenu.java
 * QSelectableMenu abstract class definition
 * 
 */

package chairosoft.quadrado.game;

// import chairosoft.quadrado.ui.graphics.Color;
import chairosoft.quadrado.ui.graphics.DrawingContext;
// import chairosoft.quadrado.ui.graphics.DrawingImage;
// import chairosoft.quadrado.ui.graphics.FontFace;
// import chairosoft.quadrado.ui.graphics.FontLayout;
// import chairosoft.quadrado.ui.graphics.WrappedText;

// import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


/**
 * A menu whose selection items can be actions or nested menus.
 */
public abstract class QSelectableMenu
    extends QDrawable 
    implements QPositionedDrawable
{
    //
    // Static Inner Classes
    //
    
    public static abstract class MenuItem
    {
        protected QSelectableMenu parentMenu = null;
        protected void setParentMenu(QSelectableMenu _parentMenu) { this.parentMenu = _parentMenu; }
        
        public final String name;
        public MenuItem(String _name)
        {
            this.name = _name; 
        }
        
        public abstract void execute();
    }
    
    public static class ActionMenuItem extends MenuItem
    {
        public final Runnable action;
        public ActionMenuItem(String _name, Runnable _action)
        {
            super(_name);
            this.action = _action;
        }
        
        @Override
        public void execute() { this.action.run(); }
    }
    
    public static class NestedMenuItem extends MenuItem
    {
        public final QSelectableMenu nestedMenu;
        public NestedMenuItem(String _name, QSelectableMenu _nestedMenu)
        {
            super(_name);
            this.nestedMenu = _nestedMenu;
        }
        
        @Override
        public void setParentMenu(QSelectableMenu _parentMenu)
        {
            super.setParentMenu(_parentMenu);
            if (this.nestedMenu != null) { this.nestedMenu.parentMenu = this.parentMenu; }
        }
        
        @Override
        public void execute() { if (this.nestedMenu != null) { this.nestedMenu.open(); } }
    }
    
    public static class ClosingMenuItem extends MenuItem
    {
        public final int parentsToClose;
        public ClosingMenuItem(String _name) { this(_name, 0); }
        public ClosingMenuItem(String _name, int _parentsToClose)
        {
            super(_name);
            this.parentsToClose = _parentsToClose;
        }
        
        @Override 
        public void execute() { if (this.parentMenu != null) { this.parentMenu.close(this.parentsToClose); } }
    }
    
    
    //
    // Instance Variables
    //
    
    public int x = 0;
    public int y = 0;
    public final String title;
    private boolean titleVisibility = false;
    private final ArrayList<MenuItem> menuItems = new ArrayList<>();
    public final List<MenuItem> readonlyMenuItems = Collections.unmodifiableList(this.menuItems);
    
    protected QSelectableMenu parentMenu = null;
    protected QSelectableMenu currentSubmenu = null;
    
    protected boolean isOpen = false;
    protected MenuItem selectedItem = null;
    public MenuItem getSelectedItem() { return this.selectedItem; }
    
    
    //
    // Constructor
    //
    
    public QSelectableMenu(String _title, MenuItem... _menuItems)
    {
        this.title = _title;
        this.addMenuItems(_menuItems);
        this.selectDefaultItem();
    }
    
    
    //
    // Instance Methods 
    //
    
    public boolean getTitleVisibility() { return this.titleVisibility; }
    public void setTitleVisibility(boolean visibility) { this.titleVisibility = visibility; }
    
    public boolean isOpen() { return this.isOpen; }
    public void open() 
    {
        this.isOpen = true;
        if (this.parentMenu != null)
        {
            this.parentMenu.currentSubmenu = this;
        }
    }
    
    public void selectDefaultItem()
    {
        this.selectedItem = this.menuItems.isEmpty()
            ? null
            : this.menuItems.get(0);
    }
    
    public void close() { this.close(0); }
    public void closeAll() { this.close(-1); }
    public void close(int parentsToClose)
    {
        this.isOpen = false;
        if (this.parentMenu != null)
        {
            this.parentMenu.currentSubmenu = null;
            if (parentsToClose != 0)
            {
                this.parentMenu.close(parentsToClose - 1);
            }
        }
    }
    
    protected void onMenuItemsChanged() { }
    
    public void addMenuItems(MenuItem... items)
    {
        int size = this.menuItems.size();
        for (MenuItem item : items)
        {
            if (item.parentMenu != null) { continue; }
            item.parentMenu = this;
            this.menuItems.add(item);
        }
        
        if (size != this.menuItems.size())
        {
            this.onMenuItemsChanged();
        }
    }
    
    public void removeMenuItems(MenuItem... items)
    {
        int size = this.menuItems.size();
        for (MenuItem item : items)
        {
            if (item.parentMenu != this) { continue; }
            item.parentMenu = null;
            this.menuItems.remove(item);
        }
        
        if (size != this.menuItems.size())
        {
            this.onMenuItemsChanged();
        }
    }
    
    public void executeSelectedItem() { if (this.selectedItem != null) { this.selectedItem.execute(); } }
    
    public final void drawToContextAtOwnPosition(DrawingContext ctx) { this.drawToContext(ctx, this.x, this.y); }
}