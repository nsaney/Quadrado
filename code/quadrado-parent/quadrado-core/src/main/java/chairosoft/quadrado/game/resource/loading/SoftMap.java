package chairosoft.quadrado.game.resource.loading;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
        synchronized (this.innerMap) {
            SoftReference<V> softReference = this.innerMap.get(key);
            if (softReference != null) {
                result = softReference.get();
            }
            if (result == null) {
                result = this.mappingFunction.apply(key);
                SoftReference<V> freshSoftReference = new SoftReference<>(result);
                this.innerMap.put(key, freshSoftReference);
            }
            this.purgeInvalidEntries();
        }
        return result;
    }
    
    public void purgeInvalidEntries() {
        synchronized (this.innerMap) {
            ArrayList<K> invalidKeys = new ArrayList<>(this.innerMap.size());
            for (K key : this.innerMap.keySet()) {
                SoftReference<V> reference = this.innerMap.get(key);
                if (reference.get() == null) {
                    invalidKeys.add(key);
                }
            }
            for (K key : invalidKeys) {
                this.innerMap.remove(key);
            }
        }
    }
    
    
    ////// Static Inner Classes //////
    public static class ByDefaultConstructor<C> extends SoftMap<Class<? extends C>, C> {
        
        //// Constructor ////
        public ByDefaultConstructor() {
            super(ByDefaultConstructor::getInstance);
        }
        
        //// Constructor Helpers ////
        public static <C> C getInstance(Class<? extends C> clazz) {
            Constructor<? extends C> constructor;
            try {
                constructor = clazz.getConstructor();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Could not find default constructor for class: " + clazz, ex);
            }
            C result;
            try {
                result = constructor.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalStateException("An error occurred while using the default constructor for class: " + clazz, ex);
            }
            return result;
        }
    }
    
}
