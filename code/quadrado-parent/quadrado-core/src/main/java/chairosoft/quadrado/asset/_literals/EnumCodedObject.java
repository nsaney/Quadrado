package chairosoft.quadrado.asset._literals;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public interface EnumCodedObject<E extends Enum<E> & EnumLiteral<E>> extends Comparable<EnumCodedObject<E>> {
    
    ////// Instance Methods - Abstract //////
    E getCode();
    
    
    ////// Instance Methods - Default //////
    @Override default int compareTo(EnumCodedObject<E> that) {
        return this.getCode().compareTo(that.getCode());
    }
    
    
    ////// Static Methods //////
    static <K extends Enum<K> & EnumLiteral<K>, V extends EnumCodedObject<K>>
    EnumMap<K, V> toMap(List<V> items) {
        return toMapWithKeys(items, EnumCodedObject::getCode);
    }
    
    static <K extends Enum<K> & EnumLiteral<K>, T extends EnumCodedObject<K>, V extends EnumCodedObject<K>>
    EnumMap<K, V> toMapWithValues(List<T> items, Function<T, V> valueGetter) {
        return toMapWithEntries(items, EnumCodedObject::getCode, valueGetter);
    }
    
    static <K extends Enum<K>, V> EnumMap<K, V> toMapWithKeys(List<V> items, Function<V, K> keyGetter) {
        return toMapWithEntries(items, keyGetter, item -> item);
    }
    
    static <K extends Enum<K>, T, V> EnumMap<K, V> toMapWithEntries(
        List<T> items,
        Function<T, K> keyGetter,
        Function<T, V> valueGetter
    ) {
        Class<K> keyType = keyGetter.apply(items.get(0)).getDeclaringClass();
        EnumMap<K, V> result = new EnumMap<>(keyType);
        for (T item : items) {
            K key = keyGetter.apply(item);
            V value = valueGetter.apply(item);
            result.put(key, value);
        }
        return result;
    }
    
}
