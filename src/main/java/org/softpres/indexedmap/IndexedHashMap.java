/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.*;
import java.util.function.*;

/**
 * An {@link IndexedMap} backed by {@link HashMap} instances.
 */
public class IndexedHashMap<K, V> implements IndexedMap<K, V> {

  private final Map<K, V> primary;
  private final List<Index<?>> indices = new LinkedList<>();

  IndexedHashMap() {
    this(new HashMap<>());
  }

  IndexedHashMap(Map<K, V> primary) {
    this.primary = primary;
  }

  @Override
  public Optional<V> select(K key) {
    return Optional.ofNullable(get(key));
  }

  @Override
  public V get(Object key) {
    Objects.requireNonNull(key);

    return primary.get(key);
  }

  @Override
  public Optional<V> insert(K key, V value) {
    return Optional.ofNullable(put(key, value));
  }

  @Override
  public V put(K key, V value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);

    V previous = primary.put(key, value);
    if (previous != null) {
      removeFromIndex(key, previous);
    }
    addToIndex(key, value);

    return previous;
  }

  private void removeFromIndex(K key, V value) {
    indices.forEach(i -> i.remove(key, value));
  }

  private void addToIndex(K key, V value) {
    indices.forEach(i -> i.add(key, value));
  }

  @Override
  public Optional<V> delete(K key) {
    Objects.requireNonNull(key);

    Optional<V> previous = Optional.ofNullable(primary.remove(key));
    if (previous.isPresent()) {
      removeFromIndex(key, previous.get());
    }
    return previous;
  }

  @Override
  public V remove(Object key) {
    // Unavoidable cast because of remove() interface signature
    // Interestingly, this does not throw a CCE until the index lambda expr
    // todo need to find a performant way to fail safely
    @SuppressWarnings("unchecked")
    K k = (K)key;

    return delete(k).orElse(null);
  }

  @Override
  public <I> Function<I, Map<K, V>> addIndex(BiFunction<K, V, Iterable<I>> view) {
    Objects.requireNonNull(view);

    Index<I> index = new Index<>(view);

    for (java.util.Map.Entry<K, V> entry : primary.entrySet()) {
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

    public Map<K, V> applyMutable(I derived) {
      return mapping.getOrDefault(derived, Collections.emptyMap());
    }

    public Map<K, V> apply(I derived) {
      Map<K, V> result = applyMutable(derived);
      if (isUninitialised(result)) {
        return result;
      } else {
        return Collections.unmodifiableMap(result);
      }
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
      Map<K, V> result = applyMutable(i);
      if (isUninitialised(result)) {
        result = new HashMap<>();
        mapping.put(i, result);
      }
      return result;
    }

    private boolean isUninitialised(Map<K, V> result) {
      return result == Collections.EMPTY_MAP;
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
    return new UnmodifiableEntrySet<>(primary.entrySet());
  }

  @Override
  public Set<K> keySet() {
    return Collections.unmodifiableSet(primary.keySet());
  }

  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(primary.values());
  }

  @Override
  public int size() {
    return primary.size();
  }

  @Override
  public boolean isEmpty() {
    return primary.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return primary.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return primary.containsValue(value);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    primary.clear();
    for (Index<?> index : indices) {
      index.mapping.clear();
    }
  }


  // Default methods are delegated to the default implementations in {@link Map}
  // in some cases, which is likely not as efficient as the {@link HashMap}
  // versions, but ensure indexes are updated. Improvement can be done on an
  // as-needed basis.

  //todo
//  @Override
//  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
//    return primary.compute(key, remappingFunction);
//  }

  //todo
//  @Override
//  public V computeIfAbsent(K key, Function<? super K, ? extends V> remappingFunction) {
//    return primary.computeIfAbsent(key, remappingFunction);
//  }

  //todo
//  @Override
//  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
//    return primary.computeIfPresent(key, remappingFunction);
//  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return primary.getOrDefault(key, defaultValue);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    primary.forEach(action);
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return IndexedMap.super.merge(key, value, remappingFunction);
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return IndexedMap.super.putIfAbsent(key, value);
  }

  @Override
  public boolean remove(Object key, Object value) {
    return IndexedMap.super.remove(key, value);
  }

  @Override
  public V replace(K key, V value) {
    return IndexedMap.super.replace(key, value);
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    return IndexedMap.super.replace(key, oldValue, newValue);
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    MapSupport.replaceAll(this, function);
  }

}
