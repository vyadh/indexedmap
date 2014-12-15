/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import java.util.*;
import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.softpres.indexedmap.animal.Animals.*;

/**
 * Unit tests for basic operations on {@link IndexedHashMap}.
 */
public class IndexedMapConstructionTest {

  @Test
  public void initialValuesAreAccessibleFromPrimary() {
    Map<Id, Animal> initial = map(dog, cat);
    IndexedMap<Id, Animal> map = new IndexedMapBuilder<Id, Animal>()
          .primary(initial).build();

    assertThat(map.get(dog.id), is(dog));
    assertThat(map.get(cat.id), is(cat));
  }

  @Test
  public void initialValuesAreStillIndexed() {
    Map<Id, Animal> initial = map(dog, cat);
    IndexedMap<Id, Animal> map = new IndexedMapBuilder<Id, Animal>()
          .primary(initial).build();
    Function<String, Map<Id, Animal>> byFood = map.addIndex((id, a) -> a.foods);

    assertThat(byFood.apply("rabbit"), is(map(dog)));
  }

}
