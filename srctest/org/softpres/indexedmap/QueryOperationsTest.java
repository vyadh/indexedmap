/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.softpres.indexedmap.animal.Animals.cat;
import static org.softpres.indexedmap.animal.Animals.dog;

/**
 * Unit tests for query operations on {@link IndexedHashMap}.
 */
public class QueryOperationsTest {

  @Test
  public void sizeIsZeroWhenEmpty() {
    assertThat(new IndexedHashMap<Id, Animal>().size(), is(0));
  }

  @Test
  public void sizeReflectsItemsInTheMap() {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);
    map.insert(cat.id, cat);

    assertThat(map.size(), is(2));
  }

  @Test
  public void isEmptyIsTrueWithNoEntries() {
    assertThat(new IndexedHashMap<Id, Animal>().isEmpty(), is(true));
  }

  @Test
  public void isEmptyIsFalseWithoutEntries() {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);

    assertThat(map.isEmpty(), is(false));
  }

  @Test
  public void containsKeysWhenInTheMap() {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);

    assertThat(map.containsKey(cat.id), is(false));
    assertThat(map.containsKey(dog.id), is(true));
  }

  @Test
  public void containsValuesWhenInTheMap() {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(cat.id, cat);

    assertThat(map.containsValue(dog), is(false));
    assertThat(map.containsValue(cat), is(true));
  }

}
