package org.softpres.deferredexample;

import org.softpres.indexedmap.IndexedMap;

interface Operation<K, V> {

  void operate(IndexedMap<K, V> map, ChangeCollector<K, V> collector);

}
