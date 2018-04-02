/* 
 * Nicholas Saney 
 * 
 * Created: December 29, 2014
 * 
 * QMapRoomLoader.java
 * QMapRoomLoader class definition
 * 
 */

package chairosoft.quadrado.resource.maproom;


import chairosoft.quadrado.ui.system.UserInterfaceProvider;

import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.DrawingContext;

import java.util.function.Consumer;


public class QMapRoomLoader implements Runnable {
    
    ////// Instance Fields //////
    public final MapLink<?> mapLink;
    public final Consumer<Result> onFinished;
    
    
    ////// Instance Properties //////
    protected Result result = null;
    public Result getResult() { return this.result; }
    
    
    ////// Constructor //////
    public QMapRoomLoader(MapLink<?> _mapLink, Consumer<Result> _onFinished) {
        if (_mapLink == null) {
            throw new NullPointerException("Given map link is null.");
        }
        this.mapLink = _mapLink;
        this.onFinished = _onFinished;
    }
    
    
    ////// Static Inner Class //////
    public static class Result {
        //// Instance Fields ////
        public final QMapRoom<?>    mapRoom;
        public final int            spawnRow;
        public final int            spawnCol;
        public final DrawingImage   contentImage;
        public final DrawingContext contentImageContext;
        
        //// Constructor ////
        public Result(QMapRoom<?> _mapRoom, int _spawnRow, int _spawnCol, DrawingImage _contentImage, DrawingContext _contentImageContext) {
            this.mapRoom = _mapRoom;
            this.spawnRow = _spawnRow;
            this.spawnCol = _spawnCol;
            this.contentImage = _contentImage;
            this.contentImageContext = _contentImageContext;
        }
    }
    
    @Override public void run() {
        QMapRoom<?> nextQMapRoom = QMapRoom.loadFor(this.mapLink.targetMapConfig);
        
        int mapWidth = nextQMapRoom.widthPixels;
        int mapHeight = nextQMapRoom.heightPixels;
        int spawnRow = nextQMapRoom.getWrappedRowValue(this.mapLink.row2);
        int spawnCol = nextQMapRoom.getWrappedColValue(this.mapLink.col2);
        //System.err.printf("[spawnRow = %s, spawnCol = %s]\n", spawnRow, spawnCol);
        
        DrawingImage contentImage = UserInterfaceProvider.get().createDrawingImage(mapWidth, mapHeight, DrawingImage.Config.ARGB_8888);
        DrawingContext contentImageContext = contentImage.getContext();
        
        Result result = new Result(nextQMapRoom, spawnRow, spawnCol, contentImage, contentImageContext);
        this.onFinished.accept(result);
    }
    
    public void startLoading() {
        Thread loaderThread = new Thread(this);
        loaderThread.start();
    }
    
}