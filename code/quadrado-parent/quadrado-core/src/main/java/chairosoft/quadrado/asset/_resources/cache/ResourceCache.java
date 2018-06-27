package chairosoft.quadrado.asset._resources.cache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

public class ResourceCache<R> {
    
    ////// Instance Fields //////
    private final HashMap<ResourceConfig<? extends R>, SoftReference<R>> innerMap = new HashMap<>();
    
    
    ////// Constructor //////
    public ResourceCache() {
        // nothing here right now
    }
    
    
    ////// Instance Methods //////
    public R loadResource(ResourceConfig<? extends R> config) {
        R result = null;
        synchronized (this.innerMap) {
            SoftReference<R> softReference = this.innerMap.get(config);
            if (softReference != null) {
                result = softReference.get();
            }
            if (result == null) {
                result = config.getResource();
                SoftReference<R> freshSoftReference = new SoftReference<>(result);
                this.innerMap.put(config, freshSoftReference);
            }
            this.purgeInvalidEntries();
        }
        return result;
    }
    
    public void purgeInvalidEntries() {
        synchronized (this.innerMap) {
            ArrayList<ResourceConfig<? extends R>> invalidKeys = new ArrayList<>(this.innerMap.size());
            for (ResourceConfig<? extends R> key : this.innerMap.keySet()) {
                SoftReference<R> reference = this.innerMap.get(key);
                if (reference.get() == null) {
                    invalidKeys.add(key);
                }
            }
            for (ResourceConfig<? extends R> key : invalidKeys) {
                this.innerMap.remove(key);
            }
        }
    }
    
}
