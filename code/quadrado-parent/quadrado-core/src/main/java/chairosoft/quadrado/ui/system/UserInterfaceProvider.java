package chairosoft.quadrado.ui.system;

import chairosoft.quadrado.ui.audio.MultitrackBackgroundAudio;
import chairosoft.quadrado.ui.audio.SoundEffectAudio;
import chairosoft.quadrado.ui.input.button.ButtonDeviceProvider;
import chairosoft.quadrado.ui.graphics.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class UserInterfaceProvider {
    
    ////// Service Loaders //////
    private static final ServiceLoader<UserInterfaceProvider> USER_INTERFACE_PROVIDER_SERVICE_LOADER = ServiceLoader.load(UserInterfaceProvider.class);
    private static final ServiceLoader<ButtonDeviceProvider> BUTTON_DEVICE_PROVIDER_SERVICE_LOADER= ServiceLoader.load(ButtonDeviceProvider.class);
    
    
    ////// Singleton //////
    private static final UserInterfaceProvider UI_PROVIDER = StreamSupport
        .stream(USER_INTERFACE_PROVIDER_SERVICE_LOADER.spliterator(), false)
        .filter(UserInterfaceProvider::isUsableOnCurrentSystem)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Unable to find a usable " + UserInterfaceProvider.class.getSimpleName()));
    public static UserInterfaceProvider get() { return UI_PROVIDER; }
    
    
    ////// Service-Loaded Collections //////
    private static <S> List<? extends S> getAll(ServiceLoader<S> serviceLoader) {
        return Collections.unmodifiableList(
            StreamSupport
                .stream(serviceLoader.spliterator(), false)
                .collect(Collectors.toList())
        );
    }
    private static final List<? extends ButtonDeviceProvider> BUTTON_DEVICE_PROVIDERS = getAll(BUTTON_DEVICE_PROVIDER_SERVICE_LOADER);
    public List<? extends ButtonDeviceProvider> getButtonDeviceProviders() {
        return BUTTON_DEVICE_PROVIDERS;
    }
    
    
    ////// Instance Methods - Abstract - Protected //////
    protected abstract Iterable<String> getRequiredSystemClasses();
    
    
    ////// Instance Methods - Abstract - Public //////
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
