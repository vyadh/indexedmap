/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides some groundwork for optimistic locking or copy-on-write strategies.
 * The supplied functions allows a use-site to provide various supplementary
 * features such as:
 * <ul>
 * <li>Comparing existing and new value to protect against changes between separated
 * reads and writes including the update of the values' revision (optimistic locking).</li>
 * <li>Provide a copy-on-write strategy for values that provides protection against
 * aliasing mutable values within the map.</li>
 * <li>Recording changes to the map, for persistence of propagation to downstream
 * systems.</li>
 * </ul>
 */
public class ManagedMap<K, V> implements Map<K, V> {

  private final Map<K, V> map;
  private final Function<V, V> onAdd;
  private final BiFunction<V, V, V> onChange;
  private final Consumer<V> onDelete;

  /**
   * Create a map, the modifications
   *
   * @param map underlying map to delegate operations to.
   * @param onAdd allow callers to take action or replace values when adding.
   * @param onChange allow callers to take action or replace values when changing value.
   * @param onDelete allow callers to take action on deletions.
   */
  public ManagedMap(
        Map<K, V> map,
        Function<V, V> onAdd,
        BiFunction<V, V, V> onChange,
        Consumer<V> onDelete) {

    this.map = map;
    this.onAdd = onAdd;
    this.onChange = onChange;
    this.onDelete = onDelete;
  }

  @Override
  public void clear() {
    map.values().forEach(onDelete::accept);
    map.clear();
  }

  @Override
  public V put(K key, V value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);

    V current = map.get(key);
    V replacement = current == null ?
          onAdd.apply(value) :
          onChange.apply(current, value);

    return map.put(key, replacement);
  }

  @Override
  public V remove(Object key) {
    V removed = map.remove(key);
    if (removed != null) {
      onDelete.accept(removed);
    }
    return removed;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    m.forEach(this::put);
    map.putAll(m);
  }

  // Default methods that need managing

//  @Override
//  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
//    map.replaceAll(function);
//  }

  // Default methods dispatched for efficiency

//  @Override
//  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
//    return map.compute(key, remappingFunction);
//  }

//  @Override
//  public V computeIfAbsent(K key, Function<? super K, ? extends V> remappingFunction) {
//    return map.computeIfAbsent(key, remappingFunction);
//  }

//  @Override
//  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
//    return map.computeIfPresent(key, remappingFunction);
//  }

//  @Override
//  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
//    return map.merge(key, value, remappingFunction);
//  }

//  @Override
//  public V putIfAbsent(K key, V value) {
//    return map.putIfAbsent(key, value);
//  }

//  @Override
//  public boolean remove(Object key, Object value) {
//    return map.remove(key, value);
//  }

//  @Override
//  public boolean replace(K key, V oldValue, V newValue) {
//    return map.replace(key, oldValue, newValue);
//  }

//  @Override
//  public V replace(K key, V value) {
//    return map.replace(key, value);
//  }


  // Read-only methods, forwarded onto map to use efficient impl where available

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return map.get(key);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    map.forEach(action);
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return map.getOrDefault(key, defaultValue);
  }

  /** Unmodifiable version. */
  @Override
  public Set<K> keySet() {
    return Collections.unmodifiableSet(map.keySet());
  }

  /** Unmodifiable version. */
  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(map.values());
  }

  /** Unmodifiable version. */
  @Override
  public Set<Entry<K, V>> entrySet() {
    Set<Entry<K, V>> entries = map.entrySet();
    if (entries instanceof UnmodifiableEntrySet) {
      return entries;
    } else {
      return new UnmodifiableEntrySet<>(entries);
    }
  }

}
