/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * We don't want mutable entries mucking up the index, so for now, do not
 * allow them to be changed.
 */
class UnmodifiableEntrySet<K, V> extends AbstractSet<Entry<K, V>> {

  private final Set<Entry<K, V>> entries;

  UnmodifiableEntrySet(Set<Entry<K, V>> entries) {
    this.entries = entries;
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return new UnmodifiableIterable<>(entries.iterator());
  }

  @Override
  public int size() {
    return entries.size();
  }

  private static class UnmodifiableIterable<K, V> implements Iterator<Entry<K, V>> {
    private final Iterator<Entry<K, V>> iterator;

    public UnmodifiableIterable(Iterator<Entry<K, V>> iterator) {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Entry<K, V> next() {
      return new UnmodifiableEntry<>(iterator.next());
    }
  }

  private static class UnmodifiableEntry<K, V> implements Entry<K, V> {
    private final Entry<K, V> entry;

    public UnmodifiableEntry(Entry<K, V> entry) {
      this.entry = entry;
    }

    @Override
    public K getKey() {
      return entry.getKey();
    }

    @Override
    public V getValue() {
      return entry.getValue();
    }

    @Override
    public V setValue(V value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
      return entry.equals(obj);
    }

    @Override
    public int hashCode() {
      return entry.hashCode();
    }

    @Override
    public String toString() {
      return entry.toString();
    }
  }

}
