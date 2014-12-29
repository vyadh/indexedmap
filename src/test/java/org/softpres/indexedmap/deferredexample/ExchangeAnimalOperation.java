/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap.deferredexample;

import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;
import org.softpres.indexedmap.IndexedMap;

import java.util.Optional;

public class ExchangeAnimalOperation implements Operation<Id, Animal> {

  private final Id sellingId;
  private final Animal buying;

  ExchangeAnimalOperation(Id sellingId, Animal buying) {
    this.sellingId = sellingId;
    this.buying = buying;
  }

  public OperationResult<Id, Animal> operate(IndexedMap<Id, Animal> map) {
    OperationResult<Id, Animal> result = OperationResult.empty();

    Optional<Animal> selling = map.select(sellingId);
    if (selling.isPresent()) {
      result.deleted(sellingId);
      applicationProcessingThatMayFail();
      result.inserted(buying.id, buying);
    }

    return result;
  }

  private void applicationProcessingThatMayFail() {
    // processing that may throw exceptions
  }

}
