package chairosoft.quadrado.game.resource.loading;

import nu.xom.Builder;
import nu.xom.Document;

import java.io.InputStream;

public abstract class InternalXmlResourceLoader<K, V> extends ResourceLoader<K, V> {
    
    ////// Constructor //////
    protected InternalXmlResourceLoader(String _directory) {
        super(true, _directory);
    }
    
    
    ////// Instance Methods - Abstract //////
    protected abstract V resolveFromXml(Document xmlDocument);
    
    
    ////// Instance Methods - Concrete //////
    @Override
    protected V resolve(InputStream resourceStream) {
        try (InputStream in = resourceStream) {
            Builder builder = new Builder();
            Document xmlDocument = builder.build(in);
            return this.resolveFromXml(xmlDocument);
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
    
}
