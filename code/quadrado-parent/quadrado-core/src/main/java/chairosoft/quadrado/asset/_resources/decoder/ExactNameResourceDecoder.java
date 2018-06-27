package chairosoft.quadrado.asset._resources.decoder;

public interface ExactNameResourceDecoder extends ResourceKeyDecoder<String> {
    ////// Instance Methods //////
    @Override
    default String getResourceName(String resourceKey) {
        return resourceKey;
    }
}
