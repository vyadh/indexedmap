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

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return withLock(readLock, () ->
          IndexedMap.super.getOrDefault(key, defaultValue));
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    withLock(readLock, () -> {
      map.forEach(action);
      return null;
    });
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    withLock(writeLock, () -> {
      // Not exactly optimal, but good enough for now
      Set<Entry<K, V>> entries = new HashSet<>(map.entrySet());
      for (Entry<K, V> entry : entries) {
        K key = entry.getKey();
        V value = entry.getValue();
        V newValue = function.apply(key, value);
        map.remove(key);
        map.put(key, newValue);
      }
      return null;
    });
  }

  private <T> T withLock(Lock lock, Supplier<T> work) {
    lock.lock();
    try {
      return work.get();
    } finally {
      lock.unlock();
    }
  }

}
