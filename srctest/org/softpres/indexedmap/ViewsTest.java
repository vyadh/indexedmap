/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.softpres.indexedmap.animal.Animals.*;

/**
 * Unit tests for basic operations on {@link IndexedHashMap}.
 */
@RunWith(Parameterized.class)
public class ViewsTest {

  private final IndexedMap<Id, Animal> map;

  public ViewsTest(IndexedMap<Id, Animal> map) {
    this.map = map;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
          { new IndexedMapBuilder<>().build() },
          { new IndexedMapBuilder<>()
                .lockStrategy(new NoReadWriteLock()).build() }
    });
  }

  @Test
  public void entrySetReturnsEntireContentsOfMap() {
    map.insert(fish.id, fish);
    map.insert(cat.id, cat);
    map.insert(dog.id, dog);

    assertThat(map.entrySet(), is(map(dog, cat, fish).entrySet()));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void entrySetDoesNotAllowClear() {
    map.entrySet().clear();
  }

  @Test (expected = UnsupportedOperationException.class)
  public void entrySetIteratorDoesNotAllowRemove() {
    map.insert(fish.id, fish);

    map.entrySet().iterator().remove();
  }

  @Test
  public void keySetReturnsAllInMap() {
    map.insert(fish.id, fish);
    map.insert(cat.id, cat);
    map.insert(dog.id, dog);

    assertThat(map.keySet(), is(map(dog, cat, fish).keySet()));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void keySetDoesNotAllowClear() {
    map.keySet().clear();
  }

  @Test (expected = UnsupportedOperationException.class)
  public void keySetIteratorDoesNotAllowRemove() {
    map.insert(fish.id, fish);

    map.keySet().iterator().remove();
  }

  @Test
  public void valuesReturnsAllInMap() {
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
    map.values().clear();
  }

  @Test (expected = UnsupportedOperationException.class)
  public void valuesIteratorDoesNotAllowRemove() {
    map.insert(fish.id, fish);

    map.values().iterator().remove();
  }

}
