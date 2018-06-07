package chairosoft.quadrado.ui.graphics;

import chairosoft.quadrado.resource.loading.ResourceLoader;
import chairosoft.quadrado.resource.loading.decoder.ExactNameResourceDecoder;
import chairosoft.quadrado.resource.loading.resolver.TrueTypeFontResolver;


public class TrueTypeFontFaceLoader
    extends ResourceLoader<String, FontFace>
    implements ExactNameResourceDecoder, TrueTypeFontResolver<String>
{
    ////// Constants //////
    public static final boolean IS_INTERNAL = true;
    public static final String RESOURCE_ROOT = "font";
    
    ////// Constructor //////
    public TrueTypeFontFaceLoader() {
        super(IS_INTERNAL, RESOURCE_ROOT, EXTENSION);
    }
}
