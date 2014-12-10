/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
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

}
