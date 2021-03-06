/* 
 * TemplateGame.java 
 * TemplateGame main and auxiliary methods
 * 
 */

package com.example;

import static chairosoft.quadrado.QCompassDirection.*;
import chairosoft.quadrado.*;
import chairosoft.ui.audio.*;
import chairosoft.ui.event.*;
import chairosoft.ui.geom.*;
import chairosoft.ui.graphics.*;

import java.io.*; 
import java.util.*;

public class TemplateGame extends QApplication
{
    //
    // Main Method
    // 
    
    public static void main(String[] args)
    {
        System.err.println("Starting game... ");
        QApplication app = new TemplateGame();
        app.gameStart();
    }
    
    
    //
    // Constructor
    //
    
    public TemplateGame() { super("Template Game Title"); }
    
    
    
    //
    // Constants
    //
    
    public static final int X_SCALING = 2;
    public static final int Y_SCALING = 2;
    @Override public int getXScaling() { return X_SCALING; }
    @Override public int getYScaling() { return Y_SCALING; }
    public static final int PANEL_WIDTH = QTileset.getTileWidth() * 8 * 2;
    public static final int PANEL_HEIGHT = QTileset.getTileHeight() * 7 * 2;
    @Override public int getPanelWidth() { return PANEL_WIDTH; }
    @Override public int getPanelHeight() { return PANEL_HEIGHT; }
    
    
    
    //
    // Instance Variables
    //
    
    public enum GameState { GAME_NOT_LOADED, MAPROOM_LOADING, MAPROOM_LOADED, MAPROOM_EXPLORATION; }
    
    protected volatile GameState gameState = GameState.GAME_NOT_LOADED;
    
    protected QCompassKeypad keypad = new QCompassKeypad();
    
    protected volatile QMapRoom.MapLink currentMapLink = null;
    protected volatile QMapRoom nextQMapRoom = null;
    protected QMapRoom qmaproom = null;
    protected DrawingImage contentImage = null;
    protected DrawingContext contentImageContext = null;
    
    protected QMapRoomExplorerSprite protagonist = new QMapRoomExplorerSprite("exampleSprite01")
    {
        @Override public void setNextState(QCompassDirection nextDirection)
        {
            switch (nextDirection)
            {
                case NORTHEAST: // passthrough
                case SOUTHEAST: // passthrough
                case EAST:      this.setCurrentState("right"); break;
                case NORTHWEST: // passthrough
                case SOUTHWEST: // passthrough
                case WEST:      this.setCurrentState("left"); break;
                default: 
            }
        }
    };
    protected int spawnRow = 0;
    protected int spawnCol = 0;
    
    protected boolean show_bounding_box = false;
    
    protected volatile boolean isPaused = false;
    protected final String pauseText = "PAUSED";
    protected int pauseWidthHalf = 0;
    protected int pauseHeightHalf = 0;
    
    protected WrappedText test__wrappedText = null;
    protected String test__textForWrapping = "Dolorem ipsum dolor sit amet, consectetur elit, Duis aute irure dolor in reprehenderit.";
    
    protected boolean test__showDialogBox = false;
    protected QDialogBox test__dialogBox = null;
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
            new Functions.Consumer<QMapRoomLoader.Result>()
            {
                @Override public void accept(QMapRoomLoader.Result result)
                {
                    TemplateGame thiz = TemplateGame.this;
                    thiz.nextQMapRoom = result.qMapRoom;
                    thiz.spawnRow = result.spawnRow;
                    thiz.spawnCol = result.spawnCol;
                    thiz.contentImage = result.contentImage;
                    thiz.contentImageContext = result.contentImageContext;
                    
                    // extra things here
                    
                    thiz.gameState = GameState.MAPROOM_LOADED;
                }
            }
        );
        loader.startLoading();
    }
    
    
    protected Runnable action_respawn = new Runnable() { @Override public void run() 
    {
        protagonist.setPositionByQTile(spawnCol, spawnRow);
    }};
    
    protected Runnable action_end_game = new Runnable() { @Override public void run() 
    { 
        gameStop();
    }};
    
    
    @Override
    protected void qGameInitialize() 
    {
        System.err.println("GAME INITIALIZED");
        //Loading.startVerbose();
        
        this.test__selectionMenu = new QTextListMenu("Fun options",
            new QSelectableMenu.ActionMenuItem("zoom", action_respawn),
            new QSelectableMenu.ActionMenuItem("end game", action_end_game),
            new QSelectableMenu.ClosingMenuItem("close")
        );
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
                        this.currentMapLink = new QMapRoom.MapLink(0, 0, "exampleMap01", 13, 2);
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
    protected void qGameUpdate() 
    {
        switch (gameState)
        {
            case GAME_NOT_LOADED: 
                break;
                
            case MAPROOM_LOADING: 
                break;
                
            case MAPROOM_LOADED: 
                qmaproom = nextQMapRoom;
                protagonist.setPositionByQTile(this.spawnCol, this.spawnRow);
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
                    protagonist.moveOneFrameIn(qmaproom);
                    currentMapLink = protagonist.getMapLinkOrNullFrom(qmaproom);
                    if (currentMapLink == null)
                    {
                        protagonist.resolveCollisionInQMapRoom(qmaproom, true, true);
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
        Font ctxFont = ctx.getFont();
        
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
                    ctx.setFont(new Font(Font.Family.SANS_SERIF, Font.Style.PLAIN, 12));
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
                            FontLayout fl = ctx.getFontLayout(new Font(Font.Family.SANS_SERIF, Font.Style.PLAIN, 10));
                            int w = PANEL_WIDTH - 20;
                            int h = 2 * fl.height();
                            this.test__dialogBox.setup(fl, this.test__textForDialog, w, h);
                            this.test__dialogBox.x = (PANEL_WIDTH - w) / 2 - this.test__dialogBox.getBorderWidth();
                            this.test__dialogBox.y = PANEL_HEIGHT - h - this.test__dialogBox.x;
                        }
                        this.test__dialogBox.advanceScrollOneClick();
                        this.test__dialogBox.drawToContextAtOwnPosition(ctx);
                    }
                    
                    // selection menus
                    if (this.test__selectionMenu.isOpen())
                    {
                        if (this.test__selectionMenu.getFontLayout() == null)
                        {
                            FontLayout fl = ctx.getFontLayout(new Font(Font.Family.SANS_SERIF, Font.Style.PLAIN, 10));
                            int h = 2 * fl.height();
                            this.test__selectionMenu.setup(fl, h);
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
            ctx.setFont(ctxFont);
            ctx.setColor(ctxColor);
        }
    }
    
    private void drawContent(DrawingContext ctx, int playerX, int playerY)
    {
        // save current graphics settings
        int ctxColor = ctx.getColor();
        Font ctxFont = ctx.getFont();
        
        try
        {
            // background and qmaproom
            ctx.setColor(qmaproom.getBackgroundColor());
            ctx.fillRect(0, 0, qmaproom.getWidthPixels(), qmaproom.getHeightPixels());
            qmaproom.drawToContext(ctx, 0, 0);
            
            // player sprite
            protagonist.advanceAnimationOneClick();
            protagonist.drawToContextAtOwnPosition(ctx);
            
            // collision box
            if (show_bounding_box) { ctx.setColor(Color.BLUE); ctx.fillPolygon(protagonist); }
            
            
            // pause message
            if (this.isPaused) 
            {
                ctx.setColor(Color.CC.RED);
                ctx.setFont(new Font(Font.Family.SANS_SERIF, Font.Style.BOLD, 14));
                if (this.pauseText == null)
                {
                    FontLayout fl = ctx.getFontLayout();
                    this.pauseWidthHalf = fl.widthOf(this.pauseText) / 2;
                    this.pauseHeightHalf = fl.height() / 2;
                }
                ctx.drawString(this.pauseText, playerX - this.pauseWidthHalf, playerY + this.pauseHeightHalf);
            }
        }
        finally
        {
            // put back graphics settings
            ctx.setFont(ctxFont);
            ctx.setColor(ctxColor);
        }
    }
    
    @Override
    protected void qGameFinish()
    {
        System.err.println("GAME FINISHED");
    }
    
}