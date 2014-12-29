/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap.deferredexample;

import org.softpres.indexedmap.IndexedMap;

/**
 * An operation to be performed on an IndexedMap. Changes to the map should
 * be described by calling operations on OperationResult. Any indices required
 * by an operation implementation should be injected in via the constructor.
 */
interface Operation<K, V> {

  OperationResult<K, V> operate(IndexedMap<K, V> map);

}
