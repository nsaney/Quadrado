package chairosoft.quadrado.resource.loading.decoder;

public interface SimpleClassNameKeyDecoder extends ResourceKeyDecoder<Class<?>> {
    ////// Constants //////
    boolean IS_INTERNAL = true;
    
    ////// Instance Methods //////
    @Override
    default String getResourceName(Class<?> resourceKey) {
        return resourceKey.getSimpleName();
    }
}
