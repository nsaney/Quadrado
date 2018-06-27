package chairosoft.quadrado.asset._resources.resolver;

import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;

import java.io.IOException;
import java.io.InputStream;

public interface TrueTypeFontResolver<K> extends ResourceValueResolver<K, FontFace> {
    
    ////// Constants //////
    String EXTENSION = "ttf";
    
    
    ////// Instance Methods //////
    @Override
    default FontFace resolve(String resourceName, InputStream resourceStream) throws IOException {
        return UserInterfaceProvider.get().createFontFace(resourceStream, resourceName);
    }
}
