package chairosoft.quadrado.game.resource.loading;

import java.util.Arrays;

public abstract class ResourceConfig<R> {
    
    ////// Instance Fields //////
    public final Class<? extends R> resourceClass;
    private final Object[] comparisonFields;
    
    ////// Constructor //////
    protected ResourceConfig(Class<? extends R> _resourceClass, Object... _comparisonFields) {
        this.resourceClass = _resourceClass;
        this.comparisonFields = new Object[1 + _comparisonFields.length];
        this.comparisonFields[0] = _resourceClass;
        System.arraycopy(_comparisonFields, 0, this.comparisonFields, 1, _comparisonFields.length);
    }
    
    ////// Instance Methods //////
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.comparisonFields);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ResourceConfig)) { return false; }
        ResourceConfig that = (ResourceConfig)obj;
        return Arrays.equals(this.comparisonFields, that.comparisonFields);
    }
    
}
