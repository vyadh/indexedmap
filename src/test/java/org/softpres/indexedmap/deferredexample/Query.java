/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap.deferredexample;

import org.softpres.indexedmap.IndexedMap;

/**
 * An query to be performed on an IndexedMap. Any indices required by
 * an operation implementation should be injected in via the constructor.
 */
public interface Query<K, V, R> {

  R execute(IndexedMap<K, V> map);

}
