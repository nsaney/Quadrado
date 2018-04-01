package chairosoft.quadrado.game.resource.loading;

import java.util.Arrays;

public abstract class ResourceConfig {
    
    ////// Instance Fields //////
    private final Object[] comparisonFields;
    
    ////// Constructor //////
    protected ResourceConfig(Object... _comparisonFields) {
        this.comparisonFields = Arrays.copyOf(_comparisonFields, _comparisonFields.length);
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
