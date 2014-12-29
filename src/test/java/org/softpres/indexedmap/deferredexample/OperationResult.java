/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap.deferredexample;

import org.softpres.indexedmap.IndexedMap;

/**
 * The result of an operation on an IndexedMap.
 */
public class OperationResult<K, V> {

  private Runnable sideEffect = () -> {};

  public static <K, V> OperationResult<K, V> empty() {
    return new OperationResult<>();
  }

  public OperationResult<K, V> inserted(K key, V value) {
    return this;
  }

  public OperationResult<K, V> deleted(K key) {
    return this;
  }

  public OperationResult<K, V> withSideEffect(Runnable runnable) {
    sideEffect = runnable;
    return this;
  }

  /**
   * Play operations (in order) back on the map. First checks
   */
  public void update(IndexedMap<K, V> map) {
  }

  public Runnable sideEffect() {
    return sideEffect;
  }

}
