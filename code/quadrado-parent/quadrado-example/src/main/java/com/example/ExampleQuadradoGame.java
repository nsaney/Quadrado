/* 
 * TemplateGame.java 
 * TemplateGame main and auxiliary methods
 * 
 */

package com.example;

import chairosoft.quadrado.asset.box_style.QBoxStyle;
import chairosoft.quadrado.asset.maproom.MapLink;
import chairosoft.quadrado.asset.maproom.QMapRoom;
import chairosoft.quadrado.asset.maproom.QMapRoomLoader;
import chairosoft.quadrado.element.QDialogBox;
import chairosoft.quadrado.element.QMapRoomExplorerSprite;
import chairosoft.quadrado.element.QSelectableMenu;
import chairosoft.quadrado.element.QTextListMenu;
import chairosoft.quadrado.ui.geom.IntPoint2D;
import chairosoft.quadrado.ui.graphics.*;
import chairosoft.quadrado.ui.input.button.ButtonEvent;
import chairosoft.quadrado.ui.input.direction.CompassDirection;
import chairosoft.quadrado.ui.input.direction.CompassKeypad;
import chairosoft.quadrado.ui.system.QApplication;
import chairosoft.quadrado.ui.system.QGameState;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import com.example.__assets.box_style.BoxStyle_01;
import com.example.__assets.box_style.BoxStyle_02;
import com.example.__assets.font.Fonts;
import com.example.__assets.maproom.ExampleMap_01;
import com.example.__assets.sprite.ExampleSprite_01;

import java.io.IOException;

import static chairosoft.quadrado.ui.input.direction.CompassDirection.*;

public class ExampleQuadradoGame extends QApplication
{
    //
    // Main Method
    // 
    
    public static void main(String[] args)
    {
        System.err.println("Starting example game... ");
        ExampleQuadradoGame app = new ExampleQuadradoGame();
        app.setRequireButtonDevice(true);
        app.gameStart(app.GAME_NOT_LOADED);
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
    
    private final ExampleQuadradoGame self = this;
    
    protected CompassKeypad keypad = new CompassKeypad();
    
    protected volatile MapLink<?> currentMapLink = null;
    protected volatile QMapRoom<?> nextMapRoom = null;
    protected QMapRoom<?> maproom = null;
    protected DrawingImage contentImage = null;
    protected DrawingContext contentImageContext = null;
    
    protected QMapRoomExplorerSprite<?,?,?> protagonist = new ExampleSprite_01()
    {
        @Override public void setNextState(CompassDirection nextDirection)
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
        this.setNextGameState(MAPROOM_LOADING);
        QMapRoomLoader loader = new QMapRoomLoader(
            currentMapLink,
            result -> {
                this.nextMapRoom = result.mapRoom;
                this.spawnRow = result.spawnRow;
                this.spawnCol = result.spawnCol;
                this.contentImage = result.contentImage;
                this.contentImageContext = result.contentImageContext;
                
                // extra things here
                
                this.setNextGameState(MAPROOM_LOADED);
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
        
        super.qButtonPressed(buttonCode);
    }
    
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
        
        super.qButtonReleased(buttonCode);
    }
    
    @Override
    protected void qGameFinish()
    {
        System.err.println("GAME FINISHED");
    }
    
    
    //
    // Game States
    //
    
    private final QGameState GAME_NOT_LOADED = new QGameState() {
        @Override
        public void buttonPressed(ButtonEvent.Code buttonCode) {
            switch (buttonCode)
            {
                // normal gameplay
                case START:
                    self.currentMapLink = new MapLink<>(0, 0, ExampleMap_01.CONFIG, 13, 2);
                    self.loadQMapRoomFromCurrentLink();
                    break;
            }
        }
        
        @Override
        public void render(DrawingContext ctx) {
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
        }
    };
    
    
    private final QGameState MAPROOM_LOADING = new QGameState() {
        @Override
        public void render(DrawingContext ctx) {
            // cycle bar
            ctx.setColor(Color.RED);
            IntPoint2D cb = new IntPoint2D(7, 12);
            ctx.drawRect(cb.x, cb.y, 102, 8); // outline
            ctx.fillRect(cb.x + 1 + (int)(framesElapsedTotal % 100), cb.y, 2, 8); // cycle
        }
    };
    
    
    private final QGameState MAPROOM_LOADED = new QGameState() {
        @Override
        public void update() {
            self.maproom = self.nextMapRoom;
            self.protagonist.setPositionByQTile(self.maproom, self.spawnCol, self.spawnRow);
            self.setNextGameState(MAPROOM_EXPLORATION);
        }
        
        @Override
        public void render(DrawingContext ctx) {
            // nothing here right now
        }
    };
    
    
    private final QGameState MAPROOM_EXPLORATION = new QGameState() {
        @Override
        public void buttonPressed(ButtonEvent.Code buttonCode) {
            if (self.isPaused)
            {
                switch (buttonCode)
                {
                    case START: self.isPaused = false; break;
                }
            }
            else if (self.test__showDialogBox && self.test__dialogBox != null)
            {
                switch (buttonCode)
                {
                    case A: self.test__dialogBox.moveScrollLines(+1, 10); break;
                    case B: self.test__dialogBox.moveScrollLines(-1, 10); break;
                    case Y: self.test__showDialogBox = false; break;
                }
            }
            else if (self.test__selectionMenu != null && self.test__selectionMenu.isOpen())
            {
                switch (buttonCode)
                {
                    case UP: self.test__selectionMenu.selectPreviousItem(); break;
                    case DOWN: self.test__selectionMenu.selectNextItem(); break;
                    case A: self.test__selectionMenu.executeSelectedItem(); break;
                    case B: self.test__selectionMenu.close(); break;
                    case X: self.test__selectionMenu.close(); break;
                }
            }
            else
            {
                switch (buttonCode)
                {
                    // normal gameplay
                    case START: self.isPaused = true; break;
                    case X: self.test__selectionMenu.open(); break;
                    case Y: self.test__showDialogBox = true; break;
                    case A: break;
                    case B: break;
                    
                    // debug switches
                    case DEBUG_1: self.show_bounding_box = !self.show_bounding_box; break;
                    
                    // debug actions
                    case DEBUG_0:
                        self.action_respawn.run();
                        break;
                    default: break;
                }
            }
        }
        
        @Override
        public void update() {
            // if (framesElapsedTotal % 250 == 0) { QCollidable.startDebug(); }
            // else { QCollidable.stopDebug(); }
            
            if (self.isPaused)
            {
                // no updates here
            }
            else if (self.test__showDialogBox && self.test__dialogBox != null)
            {
                // no updates here
            }
            else if (self.test__selectionMenu != null && self.test__selectionMenu.isOpen())
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
                    self.loadQMapRoomFromCurrentLink();
                }
            }
        }
        
        @Override
        public void render(DrawingContext ctx) {
            // background
            // this is taken care of in QApplication class (using Color.WHITE)
            
            // content graphics
            IntPoint2D p = protagonist.getIntCenterPosition();
            int clipX = self.getPanelHalfWidth() - p.x;
            int clipY = self.getPanelHalfHeight() - p.y;
            this.drawContent(contentImageContext, p.x, p.y);
            ctx.drawImage(contentImage, clipX, clipY);
            
            // dialog boxes
            if (self.test__showDialogBox)
            {
                if (self.test__dialogBox == null)
                {
                    self.test__dialogBox = new QDialogBox();
                    FontFace font;
                    try {
                        font = Fonts.MINAVYA_FIXED.load();
                        font = font.deriveBySize(14);
                    }
                    catch (IOException ioex) {
                        ioex.printStackTrace();
                        font = UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.PLAIN, 10);
                    }
                    FontLayout fl = ctx.getFontLayout(font);
                    int w = PANEL_WIDTH - 20;
                    int h = 2 * fl.height();
                    self.test__dialogBox.setup(fl, self.test__textForDialog, test__boxStyle01, w, h);
                    self.test__dialogBox.x = (PANEL_WIDTH - w) / 2 - self.test__boxStyle01.borderWidths.left;
                    self.test__dialogBox.y = PANEL_HEIGHT - h - self.test__dialogBox.x - self.test__boxStyle01.borderWidths.top - self.test__boxStyle01.borderWidths.bottom;
                }
                self.test__dialogBox.advanceScrollOneClick();
                self.test__dialogBox.drawToContextAtOwnPosition(ctx);
            }
            
            // selection menus
            if (self.test__selectionMenu.isOpen())
            {
                if (self.test__selectionMenu.getFontLayout() == null)
                {
                    FontFace sansSerifPlain10 = UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.PLAIN, 10);
                    FontLayout fl = ctx.getFontLayout(sansSerifPlain10);
                    int h = 2 * fl.height();
                    self.test__selectionMenu.setup(fl, test__boxStyle02, h);
                    self.test__selectionMenu.x = 20;
                    self.test__selectionMenu.y = PANEL_HEIGHT - h - self.test__selectionMenu.x;
                }
                self.test__selectionMenu.advanceScrollOneClick();
                self.test__selectionMenu.drawToContextAtOwnPosition(ctx);
            }
            
            // cycle bar
            ctx.setColor(Color.BLUE);
            IntPoint2D cb = new IntPoint2D(7, 4);
            ctx.drawRect(cb.x, cb.y, 102, 8); // outline
            ctx.fillRect(cb.x + 1 + (int)(framesElapsedTotal % 100), cb.y, 2, 8); // cycle
        }
        
        
        private void drawContent(DrawingContext ctx, int playerX, int playerY)
        {
            ctx.withSettingsRestored(() -> {
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
                if (self.isPaused)
                {
                    ctx.setColor(Color.CC.RED);
                    ctx.setFontFace(UserInterfaceProvider.get().createFontFace(FontFamily.SANS_SERIF, FontStyle.BOLD, 14));
                    if (self.pauseText == null) {
                        self.pauseText = "";
                    }
                    FontLayout fl = ctx.getFontLayout();
                    self.pauseWidthHalf = fl.widthOf(self.pauseText) / 2;
                    self.pauseHeightHalf = fl.height() / 2;
                    ctx.drawString(self.pauseText, playerX - self.pauseWidthHalf, playerY + self.pauseHeightHalf);
                }
            });
        }
    };
}