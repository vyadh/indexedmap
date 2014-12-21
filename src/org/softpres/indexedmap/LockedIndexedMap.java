/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An {@link IndexedMap} configured with a particular lock.
 */
class LockedIndexedMap<K, V> implements IndexedMap<K, V> {

  private final IndexedMap<K, V> map;
  private final Lock readLock;
  private final Lock writeLock;

  LockedIndexedMap(IndexedMap<K, V> map, ReadWriteLock lockStrategy) {
    this.map = map;
    this.readLock = lockStrategy.readLock();
    this.writeLock = lockStrategy.writeLock();
  }

  @Override
  public Optional<V> select(K key) {
    return withLock(readLock, () -> map.select(key));
  }

  @Override
  public V get(Object key) {
    return withLock(readLock, () -> map.get(key));
  }

  @Override
  public Optional<V> insert(K key, V value) {
    return withLock(writeLock, () -> map.insert(key, value));
  }

  @Override
  public V put(K key, V value) {
    return withLock(writeLock, () -> map.put(key, value));
  }

  @Override
  public Optional<V> delete(K key) {
    return withLock(writeLock, () -> map.delete(key));
  }

  @Override
  public V remove(Object key) {
    return withLock(writeLock, () -> map.remove(key));
  }

  @Override
  public <I> Function<I, Map<K, V>> addIndex(BiFunction<K, V, Iterable<I>> view) {
    return withLock(writeLock, () -> map.addIndex(view));
  }

  @Override
  public int size() {
    // This as well as other methods do not use the convenience withLock()
    // method to avoid the boxing and unblocking of the return value
    readLock.lock();
    try {
      return map.size();
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean isEmpty() {
    readLock.lock();
    try {
      return map.isEmpty();
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean containsKey(Object key) {
    readLock.lock();
    try {
      return map.containsKey(key);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean containsValue(Object value) {
    readLock.lock();
    try {
      return map.containsValue(value);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    writeLock.lock();
    try {
      map.putAll(m);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void clear() {
    writeLock.lock();
    try {
      map.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public Set<K> keySet() {
    return Collections.unmodifiableSet(
          withLock(readLock, () -> new HashSet<>(map.keySet())));
  }

  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(
          withLock(readLock, () -> new ArrayList<>(map.values())));
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return Collections.unmodifiableSet(
          withLock(readLock, () -> new HashSet<>(map.entrySet())));
  }

  private <T> T withLock(Lock lock, Supplier<T> work) {
    lock.lock();
    try {
      return work.get();
    } finally {
      lock.unlock();
    }
  }


  // Default methods

  @Override
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return withLock(writeLock, () -> map.compute(key, remappingFunction));
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> remappingFunction) {
    return withLock(writeLock, () -> map.computeIfAbsent(key, remappingFunction));
  }

  @Override
  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return withLock(writeLock, () -> map.computeIfPresent(key, remappingFunction));
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return withLock(readLock, () -> map.getOrDefault(key, defaultValue));
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    withLock(readLock, () -> {
      map.forEach(action);
      return null;
    });
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return withLock(writeLock, () -> map.merge(key, value, remappingFunction));
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return withLock(writeLock, () -> map.putIfAbsent(key, value));
  }

  @Override
  public boolean remove(Object key, Object value) {
    writeLock.lock();
    try {
      return map.remove(key, value);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    writeLock.lock();
    try {
      return map.replace(key, oldValue, newValue);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V replace(K key, V value) {
    writeLock.lock();
    try {
      return map.replace(key, value);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    withLock(writeLock, () -> {
      map.replaceAll(function);
      return null;
    });
  }

}
