package chairosoft.quadrado.resource.loading.decoder;

public interface ExactNameResourceDecoder extends ResourceKeyDecoder<String> {
    
    ////// Instance Methods //////
    @Override
    default String getResourceName(String resourceKey) {
        return resourceKey;
    }
    
}
