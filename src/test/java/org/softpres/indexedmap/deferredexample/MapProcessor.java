/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap.deferredexample;

import org.softpres.indexedmap.IndexedMap;
import org.softpres.indexedmap.UnmodifiableIndexedMap;

import java.util.concurrent.Executor;

/**
 * Processed operations against the IndexedMap.
 */
public class MapProcessor<K, V> {

  private final IndexedMap<K, V> map;
  private final Executor executor;

  public MapProcessor(IndexedMap<K, V> map, Executor executor) {
    this.map = map;
    this.executor = executor;
  }

  public <R> R query(Query<K, V, R> query) {
    return query.execute(new UnmodifiableIndexedMap<>(map));
  }

  public void execute(Operation<K, V> operation) {
    OperationResult<K, V> result = operation.operate(map);
    // todo optimistic locking?
    result.update(map);
    runSideEffect(result);
  }

  private void runSideEffect(OperationResult<K, V> result) {
    executor.execute(result.sideEffect());
  }

}
