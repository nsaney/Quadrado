package chairosoft.quadrado.android.system;

import android.graphics.Typeface;
import chairosoft.quadrado.android.audio.AndroidMultitrackBackgroundAudio;
import chairosoft.quadrado.android.audio.AndroidSoundEffectAudio;
import chairosoft.quadrado.android.graphics.AndroidDrawingImage;
import chairosoft.quadrado.android.graphics.AndroidFontFace;
import chairosoft.quadrado.ui.system.LifecycleUtility;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.ui.system.DoubleBufferedUI;
import chairosoft.quadrado.ui.audio.MultitrackBackgroundAudio;
import chairosoft.quadrado.ui.audio.SoundEffectAudio;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.FontFamily;
import chairosoft.quadrado.ui.graphics.FontStyle;
import chairosoft.quadrado.util.Loading;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class AndroidUserInterfaceProvider extends UserInterfaceProvider {
    
    ////// Instance Methods //////
    @Override
    protected Iterable<String> getRequiredSystemClasses() {
        return Arrays.asList(
            "android.app.Activity",
            "android.media.AudioManager"
        );
    }
    
    @Override
    public LifecycleUtility createLifecycleUtility() {
        return new AndroidLifecycleUtility();
    }
    
    @Override
    public DoubleBufferedUI createDoubleBufferedUI(
        String _title, int _width, int _height, int _xScaling, int _yScaling
    ) {
        return new AndroidDoubleBufferedUI(_title, _width, _height, _xScaling, _yScaling);
    }
    
    @Override
    public DrawingImage createDrawingImage(
        int w, int h, DrawingImage.Config c
    ) {
        return new AndroidDrawingImage(w, h, c);
    }
    
    @Override
    public DrawingImage createDrawingImage(InputStream in) throws IOException {
        return new AndroidDrawingImage(in);
    }
    
    @Override
    public FontFace createFontFace(
        String _family, FontStyle _style, int _size
    ) {
        return new AndroidFontFace(_family, _style, _size);
    }
    
    @Override
    public FontFace createFontFace(
        FontFamily logicalFamily, FontStyle _style, int _size
    ) {
        return new AndroidFontFace(logicalFamily, _style, _size);
    }
    
    @Override
    public FontFace createFontFace(InputStream fontStream, String fontName) throws IOException {
        final File tempFile = File.createTempFile(fontName, ".ttf");
        LifecycleUtility.get().deleteFileOnExit(tempFile);
        Loading.writeInputStreamToFile(fontStream, tempFile);
        Typeface typeface = Typeface.createFromFile(tempFile);
        return new AndroidFontFace(typeface);
    }
    
    @Override
    public MultitrackBackgroundAudio createMultitrackBackgroundAudio() {
        return new AndroidMultitrackBackgroundAudio();
    }
    
    @Override
    public SoundEffectAudio createSoundEffectAudio(InputStream sourceStream) {
        return new AndroidSoundEffectAudio(sourceStream);
    }
    
}
