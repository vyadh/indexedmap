/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.softpres.indexedmap.Animals.map;
import static org.softpres.indexedmap.Animals.cat;
import static org.softpres.indexedmap.Animals.dog;
import static org.softpres.indexedmap.Animals.fish;

/**
 * Unit tests for basic operations on {@link org.softpres.indexedmap.IndexedHashMap}.
 */
public class IndexedMapTest {

  @Test
  public void entrySetReturnsEntireContentsOfMap() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);
    map.insert(cat.id, cat);
    map.insert(dog.id, dog);

    assertThat(map.entrySet(), is(map(dog, cat, fish).entrySet()));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void doNotAllowClearOfTheEntrySet() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.entrySet().clear();
  }

  @Test (expected = UnsupportedOperationException.class)
  public void doNotAllowModificationOfTheEntrySetIterator() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);

    map.entrySet().iterator().remove();
  }

  @Test
  public void removeUsingObjectTypeThatIsNotValidKeyTypeReturnsNull() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    Animal previous = map.remove(new Object());

    assertThat(previous, nullValue());
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
