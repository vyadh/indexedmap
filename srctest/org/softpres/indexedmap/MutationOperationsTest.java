/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Ignore;
import org.junit.Test;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.singleton;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.softpres.indexedmap.animal.Animals.*;

/**
 * Unit tests for basic operations on {@link IndexedHashMap}.
 */
public class MutationOperationsTest {

  @Test
  public void removeUsingObjectTypeThatIsNotValidKeyTypeReturnsNull() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    Animal previous = map.remove(new Object());

    assertThat(previous, nullValue());
  }

  @Test
  public void putAllJustCallsPutForEveryEntry() {
    List<Id> putKeys = new ArrayList<>();
    List<Animal> putValues = new ArrayList<>();

    IndexedMap<Id, Animal> map = new IndexedHashMap<Id, Animal>() {
      public Animal put(Id key, Animal value) {
        putKeys.add(key);
        putValues.add(value);
        return super.put(key, value);
      }
    };

    map.putAll(map(dog, cat, fish));

    assertThat(putKeys, is(Arrays.asList(dog.id, cat.id, fish.id)));
    assertThat(putValues, is(Arrays.asList(dog, cat, fish)));
  }

  @Test
  public void clearRemovesAllEntries() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    map.put(dog.id, dog);
    map.put(cat.id, cat);

    map.clear();

    assertThat(map.isEmpty(), is(true));
  }

  @Test
  public void clearRemovesAllItemsFromExistingIndexes() {
    // Given
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    map.put(dog.id, dog);
    map.put(cat.id, cat);
    map.put(sheep.id, sheep);
    // And
    Function<String, Map<Id, Animal>> byFood = map.addIndex((id, a) -> a.foods);
    Function<Integer, Map<Id, Animal>> byLegs = map.addIndex((id, a) -> singleton(a.legs));

    // When
    map.clear();

    // Then
    assertThat(byFood.apply("biscuits").isEmpty(), is(true));
    assertThat(byLegs.apply(4).isEmpty(), is(true));
  }

  @Test
  @Ignore // Currently if we create this weird situation, it doesn't even fail atomically
  public void incompatibleObjectMatchingExistingKeyIsIgnoredAndDoesNotThrowCCE() {
    class PreviousKey extends SillyEquality { }
    class AlternateKey extends SillyEquality { }
    IndexedMap<PreviousKey, Integer> map = new IndexedHashMap<>();
    map.addIndex((k, v) -> Collections.emptyList());

    map.insert(new PreviousKey(), 1);
    Integer previous = map.remove(new AlternateKey());

    assertThat(previous, nullValue());
    assertThat(map.get(new PreviousKey()), is(1));
  }

  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  class SillyEquality {
    @Override
    public boolean equals(Object obj) {
      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }
  }

}