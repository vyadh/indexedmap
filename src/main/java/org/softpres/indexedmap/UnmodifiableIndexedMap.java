/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An indexed map that cannot be modified.
 */
public class UnmodifiableIndexedMap<K, V> extends DispatchedIndexedMap<K, V> {

  public UnmodifiableIndexedMap(IndexedMap<K, V> map) {
    super(map);
  }

  @Override
  public <I> Function<I, Map<K, V>> addIndex(BiFunction<K, V, Iterable<I>> view) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<V> delete(K key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<V> insert(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V put(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V putIfAbsent(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V replace(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }

}
