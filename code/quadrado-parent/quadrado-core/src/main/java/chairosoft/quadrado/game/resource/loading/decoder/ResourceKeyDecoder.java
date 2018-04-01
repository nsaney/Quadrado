package chairosoft.quadrado.game.resource.loading.decoder;

public interface ResourceKeyDecoder<K> {
    String getResourceName(K resourceKey);
}
