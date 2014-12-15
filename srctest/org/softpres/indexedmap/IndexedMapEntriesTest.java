/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import java.util.HashSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.softpres.indexedmap.animal.Animals.*;

/**
 * Unit tests for basic operations on {@link IndexedHashMap}.
 */
public class IndexedMapEntriesTest {

  @Test
  public void entrySetReturnsEntireContentsOfMap() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);
    map.insert(cat.id, cat);
    map.insert(dog.id, dog);

    assertThat(map.entrySet(), is(map(dog, cat, fish).entrySet()));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void entrySetDoesNotAllowClear() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.entrySet().clear();
  }

  @Test (expected = UnsupportedOperationException.class)
  public void entrySetIteratorDoesNotAllowRemove() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);

    map.entrySet().iterator().remove();
  }

  @Test
  public void keySetReturnsAllInMap() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);
    map.insert(cat.id, cat);
    map.insert(dog.id, dog);

    assertThat(map.keySet(), is(map(dog, cat, fish).keySet()));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void keySetDoesNotAllowClear() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.keySet().clear();
  }

  @Test (expected = UnsupportedOperationException.class)
  public void keySetIteratorDoesNotAllowRemove() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);

    map.keySet().iterator().remove();
  }

  @Test
  public void valuesReturnsAllInMap() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);
    map.insert(cat.id, cat);
    map.insert(dog.id, dog);

    assertThat(
          // Wrap in HashSet, as Map#values() does not support equals
          new HashSet<>(map.values()),
          is(new HashSet<>(map(dog, cat, fish).values())));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void valuesDoesNotAllowClear() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.values().clear();
  }

  @Test (expected = UnsupportedOperationException.class)
  public void valuesIteratorDoesNotAllowRemove() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(fish.id, fish);

    map.values().iterator().remove();
  }

}
