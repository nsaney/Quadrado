package chairosoft.quadrado.resource.loading.resolver;

import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;

import java.io.IOException;
import java.io.InputStream;

public interface DrawingImageResolver<K> extends ResourceValueResolver<K, DrawingImage> {
    
    ////// Constants //////
    String EXTENSION = "png";
    
    
    ////// Instance Methods //////
    @Override
    default DrawingImage resolve(String resourceName, InputStream resourceStream) throws IOException {
        return UserInterfaceProvider.get().createDrawingImage(resourceStream);
    }
    
    default DrawingImage[] loadTiledImages(K key, int transparencyRgb, int tileWidth, int tileHeight) {
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
    
    default DrawingImage loadOrEmpty(K key, int transparencyRgb) {
        DrawingImage sourceImageRaw = this.loadOrNull(key);
        if (sourceImageRaw != null) {
            return sourceImageRaw.getCloneWithTransparency(transparencyRgb);
        }
        return UserInterfaceProvider.get().createDrawingImage(0, 0, DrawingImage.Config.ARGB_8888);
    }
    
}
