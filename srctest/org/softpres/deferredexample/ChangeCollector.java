package org.softpres.deferredexample;

interface ChangeCollector<K, V> {

  void insert(K key, V value);
  void delete(K key);

}
