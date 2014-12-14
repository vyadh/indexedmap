/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Builder utility for index maps.
 */
public class IndexedMapBuilder<K, V> {

  private Map<K, V> primary = new HashMap<>();
  private ReadWriteLock lockStrategy = new ReentrantReadWriteLock();

  /**
   * Map used for the primary index. This is intended to allow application code
   * to customise the type of map used, but can also be used to seed the map
   * with an initial set of data.
   */
  public IndexedMapBuilder<K, V> primary(Map<K, V> primary) {
    this.primary = primary;
    return this;
  }

  /**
   * The locking strategy used to protect reads and writes (including index
   * updates). Note that any locking strategy allows only a "read uncommitted"
   * isolation between threads updates. If better is needed, use ScalaSTM.
   * <p/>
   * The default strategy is {@link java.util.concurrent.locks.ReentrantReadWriteLock},
   * as single-threaded performance is roughly comparable to doing no locking
   * anyway. Alternatively, {@link org.softpres.indexedmap.NoReadWriteLock} can
   * be used.
   */
  public IndexedMapBuilder<K, V> lockStrategy(ReadWriteLock lockStrategy) {
    this.lockStrategy = lockStrategy;
    return this;
  }

  /**
   * Build an indexed map with the currently configured values.
   *
   * @return fully built indexed map.
   */
  public IndexedMap<K, V> build() {
    if (isLockingEnabled()) {
      return new LockedIndexedMap<>(new IndexedHashMap<>(primary), lockStrategy);
    } else {
      return new IndexedHashMap<>(primary);
    }
  }

  private boolean isLockingEnabled() {
    return !(lockStrategy instanceof NoReadWriteLock);
  }

}
