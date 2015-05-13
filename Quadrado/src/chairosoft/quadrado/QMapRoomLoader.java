/* 
 * Nicholas Saney 
 * 
 * Created: December 29, 2014
 * 
 * QMapRoomLoader.java
 * QMapRoomLoader class definition
 * 
 */

package chairosoft.quadrado;


import chairosoft.util.function.*;

import chairosoft.ui.graphics.DrawingImage;
import chairosoft.ui.graphics.DrawingContext;

import java.io.*; 
import java.util.*;


public class QMapRoomLoader implements Runnable
{
    public final QMapRoom.MapLink mapLink;
    public final Consumer<Result> onFinished;
    
    protected Result result = null;
    public Result getResult() { return this.result; }
    
    public QMapRoomLoader(QMapRoom.MapLink _mapLink, Consumer<Result> _onFinished)
    {
        this.mapLink = _mapLink;
        this.onFinished = _onFinished;
    }
    
    public static class Result
    {
        public final QMapRoom       qMapRoom;
        public final int            spawnRow;
        public final int            spawnCol;
        public final DrawingImage   contentImage;
        public final DrawingContext contentImageContext;
        
        public Result(QMapRoom _qMapRoom, int _spawnRow, int _spawnCol, DrawingImage _contentImage, DrawingContext _contentImageContext)
        {
            this.qMapRoom = _qMapRoom;
            this.spawnRow = _spawnRow;
            this.spawnCol = _spawnCol;
            this.contentImage = _contentImage;
            this.contentImageContext = _contentImageContext;
        }
    }
    
    @Override public void run()
    {
        QMapRoom nextQMapRoom = new QMapRoom(this.mapLink.map);
        
        int mapWidth = nextQMapRoom.getWidthPixels();
        int mapHeight = nextQMapRoom.getHeightPixels();
        int spawnRow = nextQMapRoom.getWrappedRowValue(this.mapLink.row2);
        int spawnCol = nextQMapRoom.getWrappedColValue(this.mapLink.col2);
        //System.err.printf("[spawnRow = %s, spawnCol = %s]\n", spawnRow, spawnCol);
        
        DrawingImage contentImage = DrawingImage.create(mapWidth, mapHeight, DrawingImage.Config.ARGB_8888);
        DrawingContext contentImageContext = contentImage.getContext();
        
        Result result = new Result(nextQMapRoom, spawnRow, spawnCol, contentImage, contentImageContext);
        this.onFinished.accept(result);
    }
    
    public void startLoading()
    {
        Thread loaderThread = new Thread(this);
        loaderThread.start();
    }
}