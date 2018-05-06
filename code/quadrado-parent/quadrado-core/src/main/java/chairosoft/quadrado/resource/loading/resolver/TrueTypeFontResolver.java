package chairosoft.quadrado.resource.loading.resolver;

import chairosoft.quadrado.ui.graphics.FontFace;
import chairosoft.quadrado.ui.system.UserInterfaceProvider;

import java.io.IOException;
import java.io.InputStream;

public interface TrueTypeFontResolver<K> extends ResourceValueResolver<K, FontFace> {
    
    ////// Instance Methods //////
    @Override
    default FontFace resolve(String resourceName, InputStream resourceStream) throws IOException {
        return UserInterfaceProvider.get().createFontFace(resourceStream, resourceName);
    }
}
