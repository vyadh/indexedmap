/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides some groundwork for optimistic locking or copy-on-write strategies.
 * The supplied two-arg function allows a use-site to provide various supplementary
 * features such as:
 * (1) Comparing existing and new value to protect against changes between separated
 * reads and writes including the update of the values' revision (optimistic locking).
 * (2) Provide a copy-on-write strategy for values that provides protection against
 * aliasing mutable values within the map.
 */
public class ManagedWriteIndexedMap<K, V> extends DispatchedIndexedMap<K, V> {

  private final IndexedMap<K, V> map;
  private final BiFunction<V, V, V> onWrite;

  /**
   * todo
   *
   * @param map
   * @param onWrite
   */
  public ManagedWriteIndexedMap(
        IndexedMap<K, V> map,
        BiFunction<V, V, V> onWrite) {

    super(map);
    this.map = map;
    this.onWrite = onWrite;
  }

  @Override
  public <I> Function<I, Map<K, V>> addIndex(BiFunction<K, V, Iterable<I>> view) {
    return map.addIndex(view);
  }

  @Override
  public Optional<V> insert(K key, V value) {
    //todo
    return map.insert(key, value);
  }

  @Override
  public Optional<V> delete(K key) {
    return map.delete(key);
  }

  @Override
  public V put(K key, V value) {
    //todo
    return map.put(key, value);
  }

  @Override
  public V remove(Object key) {
    return map.remove(key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    //todo
    map.putAll(m);
  }

  @Override
  public void clear() {
    map.clear();
  }

}
