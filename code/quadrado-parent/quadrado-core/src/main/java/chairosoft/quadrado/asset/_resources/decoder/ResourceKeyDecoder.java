package chairosoft.quadrado.asset._resources.decoder;

public interface ResourceKeyDecoder<K> {
    String getResourceName(K resourceKey);
}
