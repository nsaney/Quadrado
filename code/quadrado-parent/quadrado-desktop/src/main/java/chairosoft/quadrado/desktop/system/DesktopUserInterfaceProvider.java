package chairosoft.quadrado.desktop.system;

import chairosoft.quadrado.desktop.audio.DesktopMultitrackBackgroundAudio;
import chairosoft.quadrado.desktop.audio.DesktopSoundEffectAudio;
import chairosoft.quadrado.desktop.graphics.DesktopDrawingImage;
import chairosoft.quadrado.desktop.graphics.DesktopFontFace;
import chairosoft.quadrado.ui.system.LifecycleUtility;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;
import chairosoft.quadrado.ui.system.DoubleBufferedUI;
import chairosoft.quadrado.ui.audio.MultitrackBackgroundAudio;
import chairosoft.quadrado.ui.audio.SoundEffectAudio;
import chairosoft.quadrado.ui.graphics.DrawingImage;
import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.graphics.FontFamily;
import chairosoft.quadrado.ui.graphics.FontStyle;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DesktopUserInterfaceProvider extends UserInterfaceProvider {
    
    ////// Instance Methods //////
    @Override
    public Iterable<String> getRequiredSystemClasses() {
        return Arrays.asList(
            "java.awt.Graphics2D",
            "javax.swing.JFrame",
            "java.awt.image.BufferedImage",
            "javax.imageio.ImageIO",
            "java.awt.Font",
            "javax.sound.sampled.AudioSystem"
        );
    }
    
    @Override
    public LifecycleUtility createLifecycleUtility() {
        return new DesktopLifecycleUtility();
    }
    
    @Override
    public DoubleBufferedUI createDoubleBufferedUI(
        String _title, int _width, int _height, int _xScaling, int _yScaling
    ) {
        return new DesktopDoubleBufferedUI(_title, _width, _height, _xScaling, _yScaling);
    }
    
    @Override
    public DrawingImage createDrawingImage(
        int w, int h, DrawingImage.Config c
    ) {
        return new DesktopDrawingImage(w, h, c);
    }
    
    @Override
    public DrawingImage createDrawingImage(InputStream in) throws IOException {
        return new DesktopDrawingImage(in);
    }
    
    @Override
    public FontFace createFontFace(String _family, FontStyle _style, int _size) {
        return new DesktopFontFace(_family, _style, _size);
    }
    
    @Override
    public FontFace createFontFace(FontFamily logicalFamily, FontStyle _style, int _size) {
        return new DesktopFontFace(logicalFamily, _style, _size);
    }
    
    @Override
    public FontFace createFontFace(InputStream fontStream, String fontName) throws IOException {
        try {
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            return new DesktopFontFace(awtFont);
        }
        catch (FontFormatException ex) {
            throw new IOException(ex);
        }
    }
    
    @Override
    public MultitrackBackgroundAudio createMultitrackBackgroundAudio() {
        return new DesktopMultitrackBackgroundAudio();
    }
    
    @Override
    public SoundEffectAudio createSoundEffectAudio(InputStream sourceStream) {
        return new DesktopSoundEffectAudio(sourceStream);
    }
    
}
