/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An {@link IndexedMap} backed by {@link HashMap} instances.
 */
public class IndexedHashMap<K, V> implements IndexedMap<K,V> {

  private final HashMap<K, V> contents = new HashMap<>();
  private final List<Index<?>> indices = new LinkedList<>();

  @Override
  public Optional<V> select(K key) {
    Objects.requireNonNull(key);

    return Optional.ofNullable(contents.get(key));
  }

  @Override
  public Optional<V> insert(K key, V value) {
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

  @Override
  public Optional<V> delete(K key) {
    Objects.requireNonNull(key);

    Optional<V> previous = Optional.ofNullable(contents.remove(key));
    previous.ifPresent(p -> indices.forEach(i -> i.remove(key, p)));
    return previous;
  }

  @Override
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

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    return Collections.unmodifiableSet(contents.entrySet());
  }

}
