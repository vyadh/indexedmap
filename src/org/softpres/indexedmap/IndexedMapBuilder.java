package org.softpres.indexedmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder utility for index maps.
 */
public class IndexedMapBuilder<K, V> {

  private Map<K, V> primary = new HashMap<>();

  public IndexedMapBuilder() {
  }

  /**
   * Map used for the primary index. This is intended to allow application code
   * to customise the type of map used, but can also be used to seed the map
   * with an initial set of data.
   *
   * @param primary
   * @return
   */
  public IndexedMapBuilder<K, V> primary(Map<K, V> primary) {
    this.primary = primary;
    return this;
  }

  /**
   * Build an indexed map with the currently configured values.
   *
   * @return
   */
  public IndexedMap<K, V> build() {
    return new IndexedHashMap<>(primary);
  }

}
