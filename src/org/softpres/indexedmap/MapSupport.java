/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Functions used across map implementations.
 */
class MapSupport {

  /**
   * Implementation of replaceAll that does not require a mutable entry set.
   */
  static <K, V> void replaceAll(
        Map<K, V> map,
        BiFunction<? super K, ? super V, ? extends V> function) {

    // Not exactly optimal, but good enough for now
    Set<Map.Entry<K, V>> entries = new HashSet<>(map.entrySet());
    for (Map.Entry<K, V> entry : entries) {
      K key = entry.getKey();
      V value = entry.getValue();
      V newValue = function.apply(key, value);
      map.put(key, newValue);
    }
  }

}
