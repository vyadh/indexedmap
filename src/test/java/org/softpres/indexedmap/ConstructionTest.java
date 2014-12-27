/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import java.util.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.softpres.indexedmap.animal.Animals.*;

/**
 * Unit tests for basic operations on {@link IndexedHashMap}.
 */
public class ConstructionTest {

  @Test
  public void initialValuesAreAccessibleFromPrimary() {
    Map<Id, Animal> initial = map(dog, cat);
    IndexedMap<Id, Animal> map = new IndexedMapBuilder<Id, Animal>()
          .primary(initial).build();

    assertThat(map.get(dog.id)).isEqualTo(dog);
    assertThat(map.get(cat.id)).isEqualTo(cat);
  }

  @Test
  public void initialValuesAreStillIndexed() {
    Map<Id, Animal> initial = map(dog, cat);
    IndexedMap<Id, Animal> map = new IndexedMapBuilder<Id, Animal>()
          .primary(initial).build();
    Function<String, Map<Id, Animal>> byFood = map.addIndex((id, a) -> a.foods);

    assertThat(byFood.apply("rabbit")).isEqualTo(map(dog));
  }

}
