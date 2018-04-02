package chairosoft.quadrado.resource.loading.cache;

import chairosoft.quadrado.util.function.ExceptionThrowingSupplier;

public class SuppliedResourceConfig<R> extends ResourceConfig<R> {
    
    ////// Instance Fields //////
    public final ExceptionThrowingSupplier<R, ?> resourceSupplier;
    
    
    ////// Constructor //////
    public SuppliedResourceConfig(ExceptionThrowingSupplier<R, ?> resourceSupplier, Object... _comparisonFields) {
        super(_comparisonFields);
        this.resourceSupplier = resourceSupplier;
    }
    
    
    ////// Instance Methods //////
    @Override
    public R getResource() {
        return this.resourceSupplier.getOrWrap(RuntimeException::new);
    }
    
}
