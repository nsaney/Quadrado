package chairosoft.quadrado.game.resource.loading;

import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.util.function.ExceptionThrowingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class DrawingImageLoader<K> extends ModularResourceLoader<K, DrawingImage> {
    
    
    ////// Constructor //////
    public DrawingImageLoader(
        boolean _isInternal,
        String _directory,
        String _extension,
        Function<K, String> _keyDecoder,
        ExceptionThrowingFunction<InputStream, DrawingImage, IOException> _streamResolver
    ) {
        super(_isInternal, _directory, _extension, _keyDecoder, _streamResolver);
    }
    
    
    ////// Instance Methods //////
    public DrawingImage[] loadTiledImages(K key, int transparencyRgb, int tileWidth, int tileHeight) {
        DrawingImage[] tiledImages;
        DrawingImage tiledImageRaw = this.loadOrNull(key);
        if (tiledImageRaw == null) {
            tiledImages = new DrawingImage[0];
        }
        else {
            DrawingImage tiledImage = tiledImageRaw.getCloneWithTransparency(transparencyRgb);
            int imageHeight = tiledImage.getHeight();
            int imageWidth = tiledImage.getWidth();
            int verticalCount = imageHeight / tileHeight;
            int horizontalCount = imageWidth / tileWidth;
            tiledImages = new DrawingImage[verticalCount * horizontalCount];
            for (int i = 0, y = 0; y < imageHeight; y += tileHeight) {
                for (int x = 0; x < imageWidth; x += tileWidth, ++i) {
                    DrawingImage tileImage = tiledImage.getImmutableSubimage(
                        x,
                        y,
                        tileWidth,
                        tileHeight
                    );
                    tiledImages[i] = tileImage;
                }
            }
        }
        return tiledImages;
    }
    
    public DrawingImage loadOrEmpty(K key, int transparencyRgb) {
        DrawingImage sourceImageRaw = this.loadOrNull(key);
        if (sourceImageRaw != null) {
            return sourceImageRaw.getCloneWithTransparency(transparencyRgb);
        }
        return UserInterfaceProvider.get().createDrawingImage(0, 0, DrawingImage.Config.ARGB_8888);
    }
}
