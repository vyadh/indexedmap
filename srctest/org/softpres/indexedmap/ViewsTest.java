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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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

    assertThat(map.entrySet()).isEqualTo(map(dog, cat, fish).entrySet());
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

  @Test (expected = UnsupportedOperationException.class)
  public void entrySetEntriesDoNotAllowModification() {
    map.insert(fish.id, fish);

    Map.Entry<Id, Animal> entry = map.entrySet().iterator().next();
    entry.setValue(dog);
  }

  @Test
  public void keySetReturnsAllInMap() {
    map.insert(fish.id, fish);
    map.insert(cat.id, cat);
    map.insert(dog.id, dog);

    assertThat(map.keySet()).containsExactly(dog.id, cat.id, fish.id);
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

    assertThat(map.values()).containsOnly(dog, cat, fish);
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
