/* 
 * TemplateGame.java 
 * TemplateGame main and auxiliary methods
 * 
 */

package com.example;

import static chairosoft.quadrado.game.QCompassDirection.*;
import chairosoft.quadrado.game.*;
import chairosoft.quadrado.game.resource.box_style.QBoxStyle;
import chairosoft.quadrado.game.resource.maproom.MapLink;
import chairosoft.quadrado.game.resource.maproom.QMapRoom;
import chairosoft.quadrado.game.resource.maproom.QMapRoomExplorerSprite;
import chairosoft.quadrado.game.resource.maproom.QMapRoomLoader;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.ui.input.*;
import chairosoft.quadrado.ui.geom.*;
import chairosoft.quadrado.ui.graphics.*;
import com.example.__resources.BoxStyle_01;
import com.example.__resources.BoxStyle_02;
import com.example.__resources.ExampleMap_01;
import com.example.__resources.ExampleSprite_01;
import com.example.__resources.TileSet_01;

public class ExampleQuadradoGame extends QApplication
{
    //
    // Main Method
    // 
    
    public static void main(String[] args)
    {
        System.err.println("Starting example game... ");
        QApplication app = new ExampleQuadradoGame();
        app.setRequireButtonDevice(true);
        app.gameStart();
    }
    
    
    //
    // Constructor
    //
    
    public ExampleQuadradoGame() {
        super("Example Game Title");
    }
    
    
    
    //
    // Constants
    //
    
    public static final int X_SCALING = 2;
    public static final int Y_SCALING = 2;
    @Override public int getXScaling() { return X_SCALING; }
    @Override public int getYScaling() { return Y_SCALING; }
    public static final int PANEL_WIDTH = 16 * 8 * 2;
    public static final int PANEL_HEIGHT = 16 * 7 * 2;
    @Override public int getPanelWidth() { return PANEL_WIDTH; }
    @Override public int getPanelHeight() { return PANEL_HEIGHT; }
    
    
    
    //
    // Instance Variables
    //
    
    public enum GameState { GAME_NOT_LOADED, MAPROOM_LOADING, MAPROOM_LOADED, MAPROOM_EXPLORATION; }
    
    protected volatile GameState gameState = GameState.GAME_NOT_LOADED;
    
    protected QCompassKeypad keypad = new QCompassKeypad();
    
    protected volatile MapLink<?> currentMapLink = null;
    protected volatile QMapRoom<?> nextMapRoom = null;
    protected QMapRoom<?> maproom = null;
    protected DrawingImage contentImage = null;
    protected DrawingContext contentImageContext = null;
    
    protected QMapRoomExplorerSprite<?,?,?> protagonist = new ExampleSprite_01()
    {
        @Override public void setNextState(QCompassDirection nextDirection)
        {
            switch (nextDirection)
            {
                case NORTHEAST: // passthrough
                case SOUTHEAST: // passthrough
                case EAST:      this.setCurrentStateCode(StateCode.right); break;
                case NORTHWEST: // passthrough
                case SOUTHWEST: // passthrough
                case WEST:      this.setCurrentStateCode(StateCode.left); break;
                default: 
            }
        }
    };
    protected int spawnRow = 0;
    protected int spawnCol = 0;
    
    protected boolean show_bounding_box = false;
    
    protected volatile boolean isPaused = false;
    protected String pauseText = "PAUSED";
    protected int pauseWidthHalf = 0;
    protected int pauseHeightHalf = 0;
    
    protected WrappedText test__wrappedText = null;
    protected String test__textForWrapping = "Dolorem ipsum dolor sit amet, consectetur elit, Duis aute irure dolor in reprehenderit.";
    
    protected boolean test__showDialogBox = false;
    protected QDialogBox test__dialogBox = null;
    protected QBoxStyle test__boxStyle01 = new BoxStyle_01();
    protected QBoxStyle test__boxStyle02 = new BoxStyle_02();
    protected String test__textForDialog = "Test dialog... " +
        "Lorem ipsum dolor sit amet, consectetur adipisicing elit, " + 
        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " + 
        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " + 
        "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " + 
        "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla " + 
        "pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " + 
        "qui officia deserunt mollit anim id est laborum.";
    
    protected QTextListMenu test__selectionMenu = null;
    
    
    //
    // Instance Methods 
    //
    
    public void loadQMapRoomFromCurrentLink()
    {
        this.gameState = GameState.MAPROOM_LOADING;
        QMapRoomLoader loader = new QMapRoomLoader(
            currentMapLink,
            result -> {
                this.nextMapRoom = result.mapRoom;
                this.spawnRow = result.spawnRow;
                this.spawnCol = result.spawnCol;
                this.contentImage = result.contentImage;
                this.contentImageContext = result.contentImageContext;
                
                // extra things here
                
                this.gameState = GameState.MAPROOM_LOADED;
            }
        );
        loader.startLoading();
    }
    
    
    protected Runnable action_respawn = () -> protagonist.setPositionByQTile(this.maproom, spawnCol, spawnRow);
    
    protected Runnable action_end_game = this::gameStop;
    
    
    @Override
    protected void qGameInitialize() 
    {
        //Loading.startVerbose();
        
        this.test__selectionMenu = new QTextListMenu("Fun options",
            new QSelectableMenu.ActionMenuItem("zoom", action_respawn),
            new QSelectableMenu.ActionMenuItem("end game", action_end_game),
            new QSelectableMenu.ClosingMenuItem("close")
        );
        
        System.err.println("GAME INITIALIZED");
    }
    
    @Override
    protected void qGameUpdateInit() { }
    
    @Override
    protected void qButtonPressed(ButtonEvent.Code buttonCode) 
    {
        // keypad needs to respond, regardless of game state
        switch (buttonCode)
        {
            case LEFT:  keypad.activateValue(WEST); break;
            case RIGHT: keypad.activateValue(EAST); break;
            case UP:    keypad.activateValue(NORTH); break;
            case DOWN:  keypad.activateValue(SOUTH); break;
            
            // debug always
            case DEBUG_9: this.showDebug = !this.showDebug; break;
        }
        
        switch (gameState)
        {
            case GAME_NOT_LOADED:
                switch (buttonCode)
                {
                    // normal gameplay
                    case START:
                        this.currentMapLink = new MapLink<TileSet_01.TileCode>(0, 0, ExampleMap_01::new, 13, 2);
                        this.loadQMapRoomFromCurrentLink();
                        break;
                }
                break;
            
            case MAPROOM_EXPLORATION:
                if (this.isPaused)
                {
                    switch (buttonCode)
                    {
                        case START: this.isPaused = false; break;
                    }
                }
                else if (this.test__showDialogBox && this.test__dialogBox != null)
                {
                    switch (buttonCode)
                    {
                        case A: this.test__dialogBox.moveScrollLines(+1, 10); break;
                        case B: this.test__dialogBox.moveScrollLines(-1, 10); break;
                        case Y: this.test__showDialogBox = false; break;
                    }
                }
                else if (this.test__selectionMenu != null && this.test__selectionMenu.isOpen())
                {
                    switch (buttonCode)
                    {
                        case UP: this.test__selectionMenu.selectPreviousItem(); break;
                        case DOWN: this.test__selectionMenu.selectNextItem(); break;
                        case A: this.test__selectionMenu.executeSelectedItem(); break;
                        case B: this.test__selectionMenu.close(); break;
                        case X: this.test__selectionMenu.close(); break;
                    }
                }
                else
                {
                    switch (buttonCode)
                    {
                        // normal gameplay
                        case START: this.isPaused = true; break;
                        case X: this.test__selectionMenu.open(); break;
                        case Y: this.test__showDialogBox = true; break;
                        case A: break;
                        case B: break;
                        
                        // debug switches
                        case DEBUG_1: this.show_bounding_box = !this.show_bounding_box; break;
                        
                        // debug actions
                        case DEBUG_0: 
                            this.action_respawn.run();
                            break;
                        default: break;
                    }
                }
                break;
        }
    }
    
    @Override
    protected void qButtonHeld(ButtonEvent.Code buttonCode) { }
    
    @Override
    protected void qButtonReleased(ButtonEvent.Code buttonCode) 
    {
        // keypad needs to respond, regardless of game state
        switch (buttonCode)
        {
            case LEFT:  keypad.deactivateValue(WEST); break;
            case RIGHT: keypad.deactivateValue(EAST); break;
            case UP:    keypad.deactivateValue(NORTH); break;
            case DOWN:  keypad.deactivateValue(SOUTH); break;
        }
    }
    
    @Override
    protected void qButtonNotHeld(ButtonEvent.Code buttonCode) { }
    
    @Override
    protected void qPointerPressed(float x, float y) { }
    
    @Override
    protected void qPointerMoved(float x, float y) { }
    
    @Override
    protected void qPointerReleased(float x, float y) { }
    
    @Override
    protected void qGameUpdate() 
    {
        switch (gameState)
        {
            case GAME_NOT_LOADED: 
                break;
                
            case MAPROOM_LOADING: 
                break;
                
            case MAPROOM_LOADED: 
                maproom = nextMapRoom;
                protagonist.setPositionByQTile(this.maproom, this.spawnCol, this.spawnRow);
                gameState = GameState.MAPROOM_EXPLORATION;
                break;
                
            case MAPROOM_EXPLORATION: 
                // if (framesElapsedTotal % 250 == 0) { QCollidable.startDebug(); }
                // else { QCollidable.stopDebug(); }
                
                if (this.isPaused)
                {
                    // no updates here
                }
                else if (this.test__showDialogBox && this.test__dialogBox != null)
                {
                    // no updates here
                }
                else if (this.test__selectionMenu != null && this.test__selectionMenu.isOpen())
                {
                    // no updates here
                }
                else
                {
                    // set velocity based on keypad compass direction
                    protagonist.setDirection(keypad.getDirection());
                    
                    // collision
                    protagonist.moveOneFrameIn(maproom);
                    currentMapLink = maproom.getCollidingMapLinkOrNull(protagonist);
                    if (currentMapLink == null)
                    {
                        protagonist.resolveCollisionInQMapRoom(maproom, true, true);
                    }
                    else
                    {
                        //System.err.println(currentMapLink);
                        this.loadQMapRoomFromCurrentLink();
                    }
                }
                break;
        }
    }
    
    @Override
    protected void qGameRender(DrawingContext ctx) 
    {
        // save current graphics settings
        int ctxColor = ctx.getColor();
        FontFace ctxFont = ctx.getFontFace();
        
        try
        {
            switch (gameState)
            {
                case GAME_NOT_LOADED: 
                {
                    // cycle bar
                    ctx.setColor(Color.BLACK);
                    IntPoint2D cb = new IntPoint2D(7, 20);
                    ctx.drawRect(cb.x, cb.y, 102, 8); // outline
                    ctx.fillRect(cb.x + 1 + (int)(framesElapsedTotal % 100), cb.y, 2, 8); // cycle
                    
                    ctx.setColor(Color.CC.MAROON);
                    ctx.setFontFace(UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.PLAIN, 12));
                    int test_wrappingWidth = 200;
                    if (test__wrappedText == null)
                    {
                        test__wrappedText = ctx.getWrappedText(test__textForWrapping, test_wrappingWidth);
                    }
                    ctx.drawWrappedText(test__wrappedText, cb.x, cb.y + 10);
                    
                    ctx.setColor(Color.CC.BLACK);
                    ctx.drawRect(cb.x, cb.y + 10, test_wrappingWidth, PANEL_HEIGHT - 2 * (cb.y + 10));
                    
                    break;
                }
                case MAPROOM_LOADING: 
                {
                    // cycle bar
                    ctx.setColor(Color.RED);
                    IntPoint2D cb = new IntPoint2D(7, 12);
                    ctx.drawRect(cb.x, cb.y, 102, 8); // outline
                    ctx.fillRect(cb.x + 1 + (int)(framesElapsedTotal % 100), cb.y, 2, 8); // cycle
                    break;
                }
                case MAPROOM_LOADED: 
                    break;
                    
                case MAPROOM_EXPLORATION: 
                {
                    // background 
                    // this is taken care of in QApplication class (using Color.WHITE)
                    
                    // content graphics 
                    IntPoint2D p = protagonist.getIntCenterPosition();
                    int clipX = this.getPanelHalfWidth() - p.x;
                    int clipY = this.getPanelHalfHeight() - p.y;
                    this.drawContent(contentImageContext, p.x, p.y);
                    ctx.drawImage(contentImage, clipX, clipY);
                    
                    // dialog boxes
                    if (this.test__showDialogBox)
                    {
                        if (this.test__dialogBox == null)
                        {
                            this.test__dialogBox = new QDialogBox();
                            FontLayout fl = ctx.getFontLayout(UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.PLAIN, 10));
                            int w = PANEL_WIDTH - 20;
                            int h = 2 * fl.height();
                            this.test__dialogBox.setup(fl, this.test__textForDialog, test__boxStyle01, w, h);
                            this.test__dialogBox.x = (PANEL_WIDTH - w) / 2 - this.test__boxStyle01.borderWidths.left;
                            this.test__dialogBox.y = PANEL_HEIGHT - h - this.test__dialogBox.x - this.test__boxStyle01.borderWidths.top - this.test__boxStyle01.borderWidths.bottom;
                        }
                        this.test__dialogBox.advanceScrollOneClick();
                        this.test__dialogBox.drawToContextAtOwnPosition(ctx);
                    }
                    
                    // selection menus
                    if (this.test__selectionMenu.isOpen())
                    {
                        if (this.test__selectionMenu.getFontLayout() == null)
                        {
                            FontFace sansSerifPlain10 = UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.PLAIN, 10);
                            FontLayout fl = ctx.getFontLayout(sansSerifPlain10);
                            int h = 2 * fl.height();
                            this.test__selectionMenu.setup(fl, test__boxStyle02, h);
                            this.test__selectionMenu.x = 20;
                            this.test__selectionMenu.y = PANEL_HEIGHT - h - this.test__selectionMenu.x;
                        }
                        this.test__selectionMenu.advanceScrollOneClick();
                        this.test__selectionMenu.drawToContextAtOwnPosition(ctx);
                    }
                    
                    // cycle bar
                    ctx.setColor(Color.BLUE);
                    IntPoint2D cb = new IntPoint2D(7, 4);
                    ctx.drawRect(cb.x, cb.y, 102, 8); // outline
                    ctx.fillRect(cb.x + 1 + (int)(framesElapsedTotal % 100), cb.y, 2, 8); // cycle
                    
                    break;
                }
            }
        }
        finally
        {
            // put back graphics settings
            ctx.setFontFace(ctxFont);
            ctx.setColor(ctxColor);
        }
    }
    
    private void drawContent(DrawingContext ctx, int playerX, int playerY)
    {
        // save current graphics settings
        int ctxColor = ctx.getColor();
        FontFace ctxFontFace = ctx.getFontFace();
        
        try
        {
            // background and maproom
            ctx.setColor(maproom.backgroundColor);
            ctx.fillRect(0, 0, maproom.widthPixels, maproom.heightPixels);
            maproom.drawToContext(ctx, 0, 0);
            
            // player sprite
            protagonist.advanceAnimationOneClick();
            protagonist.drawToContextAtOwnPosition(ctx);
            
            // collision box
            if (show_bounding_box) { ctx.setColor(Color.BLUE); ctx.fillPolygon(protagonist); }
            
            
            // pause message
            if (this.isPaused) 
            {
                ctx.setColor(Color.CC.RED);
                ctx.setFontFace(UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.BOLD, 14));
                if (this.pauseText == null) {
                    this.pauseText = "";
                }
                FontLayout fl = ctx.getFontLayout();
                this.pauseWidthHalf = fl.widthOf(this.pauseText) / 2;
                this.pauseHeightHalf = fl.height() / 2;
                ctx.drawString(this.pauseText, playerX - this.pauseWidthHalf, playerY + this.pauseHeightHalf);
            }
        }
        finally
        {
            // put back graphics settings
            ctx.setFontFace(ctxFontFace);
            ctx.setColor(ctxColor);
        }
    }
    
    @Override
    protected void qGameFinish()
    {
        System.err.println("GAME FINISHED");
    }
    
}