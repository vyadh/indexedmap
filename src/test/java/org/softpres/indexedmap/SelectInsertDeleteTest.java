/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.softpres.indexedmap.animal.Animals.*;

/**
 * Unit tests for basic operations on {@link IndexedHashMap}.
 */
public class SelectInsertDeleteTest {

  @Test
  public void selectingItemThatHasBeenPreviouslyInserted() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    Id id = new Id(42);

    map.insert(id, dog);

    assertThat(map.select(id)).isEqualTo(Optional.of(dog));
  }

  @Test
  public void insertReplacesPreviousItem() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    Id id = new Id(42);

    map.insert(id, cat);
    Optional<Animal> previous = map.insert(id, dog);

    assertThat(previous).isEqualTo(Optional.of(cat));
  }

  @Test
  public void insertNewItemReturnsEmptyOptional() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    Id id = new Id(42);

    Optional<Animal> previous = map.insert(id, dog);

    assertThat(previous).isEqualTo((Optional.empty()));
  }

  @Test
  public void deleteWithoutPreviousItemReturnsEmpty() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    Id id = new Id(42);

    Optional<Animal> previous = map.delete(id);

    assertThat(previous).isEqualTo(Optional.empty());
  }

  @Test
  public void deleteWithPreviousItemReturnsPrevious() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    Id id = new Id(53);

    map.insert(id, dog);
    Optional<Animal> previous = map.delete(id);

    assertThat(previous).isEqualTo((Optional.of(dog)));
  }

}
