package chairosoft.quadrado.ui;

import chairosoft.quadrado.ui.audio.MultitrackBackgroundAudio;
import chairosoft.quadrado.ui.audio.SoundEffectAudio;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.FontFamily;
import chairosoft.quadrado.ui.graphics.FontStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public abstract class UserInterfaceProvider {
    
    ////// Static Properties //////
    private final static ServiceLoader<UserInterfaceProvider> SERVICE_LOADER = ServiceLoader.load(UserInterfaceProvider.class);
    private final static UserInterfaceProvider PROVIDER = StreamSupport.stream(SERVICE_LOADER.spliterator(), false)
        .filter(UserInterfaceProvider::isUsableOnCurrentSystem)
        .findFirst()
        .orElse(null);
    public static UserInterfaceProvider get() { return PROVIDER; }
    
    
    ////// Instance Methods - Abstract //////
    protected abstract Iterable<String> getRequiredSystemClasses();
    public abstract LifecycleUtility createLifecycleUtility();
    public abstract DoubleBufferedUI createDoubleBufferedUI(String _title, int _width, int _height, int _xScaling, int _yScaling);
    public abstract DrawingImage createDrawingImage(int w, int h, DrawingImage.Config c);
    public abstract DrawingImage createDrawingImage(InputStream in) throws IOException;
    public abstract FontFace createFontFace(String _family, FontStyle _style, int _size);
    public abstract FontFace createFontFace(FontFamily logicalFamily, FontStyle _style, int _size);
    public abstract FontFace createFontFace(InputStream fontStream, String fontName) throws IOException;
    public abstract MultitrackBackgroundAudio createMultitrackBackgroundAudio();
    public abstract SoundEffectAudio createSoundEffectAudio(InputStream sourceStream);
    
    
    ////// Instance Methods - Concrete //////
    public boolean isUsableOnCurrentSystem() {
        boolean result = true;
        Iterable<String> requiredSystemClasses = this.getRequiredSystemClasses();
        try {
            for (String className : requiredSystemClasses) {
                Class.forName(className);
            }
        }
        catch (Exception ex) {
            result = false;
        }
        return result;
    }
    
}
