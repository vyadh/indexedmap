/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.softpres.animal.Animal;
import org.softpres.animal.Id;

import java.util.*;
import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.softpres.animal.Animals.*;

/**
 * Unit tests for basic operations on {@link IndexedHashMap}.
 */
public class IndexedMapConstructionTest {

  @Test
  public void initialValuesAreAccessibleFromPrimary() {
    Map<Id, Animal> initial = map(dog, cat);
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>(initial);

    assertThat(map.get(dog.id), is(dog));
    assertThat(map.get(cat.id), is(cat));
  }

  @Test
  public void initialValuesAreStillIndexed() {
    Map<Id, Animal> initial = map(dog, cat);
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>(initial);
    Function<String, Map<Id, Animal>> byFood = map.addIndex((id, a) -> a.foods);

    assertThat(byFood.apply("rabbit"), is(map(dog)));
  }

}