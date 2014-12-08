/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.*;
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
public class IndexedMap<K, V> {

  private final HashMap<K, V> contents = new HashMap<>();
  private final List<Index<?>> indices = new LinkedList<>();

  /**
   * Get the value associated with the supplied key on the primary index.
   *
   * @param key to lookup the value in primary index.
   * @return the value associated, if one exists
   */
  public Optional<V> get(K key) {
    Objects.requireNonNull(key);

    return Optional.ofNullable(contents.get(key));
  }

  /**
   * Add or replace an entry for this key in the primary index, and update all secondary
   * indices, which may include removals as well as additions.
   *
   * @param key key for the value in the primary index
   * @param value new or replacement value to be associated
   * @return the previous value, if one existed
   */
  public Optional<V> put(K key, V value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);

    Optional<V> prev = Optional.ofNullable(contents.put(key, value));
    if (prev.isPresent()) {
      for (Index<?> i : indices) {
        i.remove(key, prev.get());
      }
    }
    for (Index<?> index : indices) {
      index.add(key, value);
    }
    return prev;
  }

  /**
   * Remove the entry in the primary index with the specified key, and update any secondary
   * indices to remove the associations.
   *
   * @param key for the entry to remove
   * @return the previous value, if one existed
   */
  public Optional<V> remove(K key) {
    Objects.requireNonNull(key);

    Optional<V> previous = Optional.ofNullable(contents.remove(key));
    previous.ifPresent(p -> indices.forEach(i -> i.remove(key, p)));
    return previous;
  }

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
  public <I> Function<I, Map<K, V>> addIndex(BiFunction<K, V, Iterable<I>> view) {
    Objects.requireNonNull(view);

    Index<I> index = new Index<>(view);
    for (java.util.Map.Entry<K, V> entry : contents.entrySet()) {
      index.add(entry.getKey(), entry.getValue());
    }
    indices.add(index);
    return index;
  }


  private class Index<I> implements Function<I, Map<K, V>> {

    private final BiFunction<K, V, Iterable<I>> view;
    private final Map<I, Map<K, V>> mapping = new HashMap<>();

    Index(BiFunction<K, V, Iterable<I>> view) {
      this.view = view;
    }

    public Map<K, V> apply(I derived) {
      return mapping.getOrDefault(derived, Collections.emptyMap());
    }

    void add(K key, V value) {
      for (I i : view.apply(key, value)) {
        associate(i).put(key, value);
      }
    }

    /**
     * Get values for secondary index value, or associate a fresh mutable map if
     * we were using the placeholder empty one.
     */
    private Map<K, V> associate(I i) {
      Map<K, V> result = apply(i);
      if (result == Collections.EMPTY_MAP) {
        result = new HashMap<>();
        mapping.put(i, result);
      }
      return result;
    }

    /**
     * Remove the entry from the index values, removing the index itself if it
     * is empty.
     */
    void remove(K key, V value) {
      for (I i : view.apply(key, value)) {
        Map<K, V> indexed = mapping.get(i);
        indexed.remove(key);
        if (indexed.isEmpty()) {
          mapping.remove(i);
        }
      }
    }

  }

}
