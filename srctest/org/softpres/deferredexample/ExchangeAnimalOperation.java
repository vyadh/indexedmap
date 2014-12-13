package org.softpres.deferredexample;

import org.softpres.animal.Animal;
import org.softpres.animal.Id;
import org.softpres.indexedmap.IndexedMap;

import java.util.Optional;

public class ExchangeAnimalOperation implements Operation<Id, Animal> {

  private final Id sellingId;
  private final Animal buying;

  ExchangeAnimalOperation(Id sellingId, Animal buying) {
    this.sellingId = sellingId;
    this.buying = buying;
  }

  public void operate(
        IndexedMap<Id, Animal> map,
        ChangeCollector<Id, Animal> collector) {

    Optional<Animal> selling = map.select(sellingId);
    if (selling.isPresent()) {
      collector.delete(sellingId);
      applicationProcessingThatMayFail();
      collector.insert(buying.id, buying);
    }
  }

  private void applicationProcessingThatMayFail() {
    // processing that may throw exceptions
  }

}
