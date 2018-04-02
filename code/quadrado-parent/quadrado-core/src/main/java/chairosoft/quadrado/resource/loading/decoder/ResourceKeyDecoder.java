package chairosoft.quadrado.resource.loading.decoder;

public interface ResourceKeyDecoder<K> {
    String getResourceName(K resourceKey);
}
