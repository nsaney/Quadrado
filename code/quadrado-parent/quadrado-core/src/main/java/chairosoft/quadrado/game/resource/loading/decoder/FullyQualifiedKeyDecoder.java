package chairosoft.quadrado.game.resource.loading.decoder;

public interface FullyQualifiedKeyDecoder extends ResourceKeyDecoder<Class<?>> {
    
    ////// Constants //////
    boolean IS_INTERNAL = true;
    String RESOURCE_ROOT = "qualified";
    
    @Override
    default String getResourceName(Class<?> resourceKey) {
        return resourceKey.getName();
    }
}
