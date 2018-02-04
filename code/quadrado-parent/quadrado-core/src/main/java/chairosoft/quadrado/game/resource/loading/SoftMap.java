package chairosoft.quadrado.game.resource.loading;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.function.Function;

public class SoftMap<K, V> {
    
    ////// Instance Fields //////
    public final Function<? super K, ? extends V> mappingFunction;
    private final HashMap<K, SoftReference<V>> innerMap = new HashMap<>();
    
    
    ////// Constructor //////
    public SoftMap(Function<? super K, ? extends V> _mappingFunction) {
        this.mappingFunction = _mappingFunction;
    }
    
    
    ////// Instance Methods //////
    public V get(K key) {
        V result = null;
        SoftReference<V> softReference = this.innerMap.get(key);
        if (softReference != null) {
            result = softReference.get();
        }
        if (result == null) {
            result = this.mappingFunction.apply(key);
            SoftReference<V> freshSoftReference = new SoftReference<>(result);
            this.innerMap.put(key, freshSoftReference);
        }
        return result;
    }
    
}
