/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A Map-like class, which allows addition of custom indices that are always kept up-to-date.
 * This can be thought of as a map with a primary index (the natural and direct key -> value lookup),
 * and zero or many secondary indexes (whatever index functions have been added).
 * <p/>
 * This is simplified concept, inspired by <a href="http://nbronson.github.io/scala-stm/indexed_map.html">
 * ScalaSTM IndexedMap</a> but without the excellent STM features.
 * <p/>
 * This means that this class is not thread safe, any and all processing is expected to be done on a
 * single thread. In particular, map access is not synchronised, and the results from an index lookup
 * may change if queried outside the processing thread.
 * <p/>
 * This class should not be used with mutable objects. If required, some sort of copy-on-write
 * strategy should be used on top.
 * <p/>
 * This class does not support nulls. Behaviour with use of nulls is undefined.
 *
 * @param <K> type of key.
 * @param <V> type of value.
 */
public interface IndexedMap<K, V> {

  /**
   * Get the value associated with the supplied key on the primary index.
   *
   * @param key to lookup the value in primary index.
   * @return the value associated, if one exists
   */
  Optional<V> select(K key);

  /**
   * Add or replace an entry for this key in the primary index, and update all secondary
   * indices, which may include removals as well as additions.
   *
   * @param key key for the value in the primary index
   * @param value new or replacement value to be associated
   * @return the previous value, if one existed
   */
  Optional<V> insert(K key, V value);

  /**
   * Remove the entry in the primary index with the specified key, and update any secondary
   * indices to remove the associations.
   *
   * @param key for the entry to remove
   * @return the previous value, if one existed
   */
  Optional<V> delete(K key);

  /**
   * Add a secondary index to this map. The secondary index is creating using the supplied function,
   * from entries (key and value) to the secondary key. The returning function can then be used to
   * allow lookup using the secondary key of type {@link I}.
   * Any existing values in the map will be indexed by this function ready for lookup.
   *
   * @param view view function from key-value entry to secondary index keys.
   * @param <I> type of index keys for lookup.
   * @return index function, allowing lookup of all entries for the supplied secondary index key.
   */
  <I> Function<I, Map<K, V>> addIndex(BiFunction<K, V, Iterable<I>> view);

}
