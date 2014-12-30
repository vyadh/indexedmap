/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;
import org.softpres.indexedmap.animal.Animal;
import org.softpres.indexedmap.animal.Id;

import java.util.*;
import java.util.function.Function;

import static org.softpres.indexedmap.animal.Animals.*;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for index methods on {@link IndexedHashMap}.
 */
public class IndexingTest {

  @Test
  public void indexByUnknownFoodReturnsNoAnimals() {
    IndexedMap<Id, Animal> map = mapWithAnimals();

    Function<String, Map<Id, Animal>> index =
          map.addIndex((id, a) -> a.foods);

    assertThat(index.apply("sausage")).isEqualTo(map());
  }

  @Test
  public void animalsAreIndexedByDifferentFoods() {
    IndexedMap<Id, Animal> map = mapWithAnimals();

    Function<String, Map<Id, Animal>> index =
          map.addIndex((id, a) -> a.foods);

    assertThat(index.apply("biscuits")).isEqualTo(map(cat, dog));
    assertThat(index.apply("fish")).isEqualTo(map(cat));
    assertThat(index.apply("mouse")).isEqualTo(map(cat));
    assertThat(index.apply("rabbit")).isEqualTo(map(dog));
    assertThat(index.apply("grass")).isEqualTo(map(cow, sheep));
    assertThat(index.apply("worm")).isEqualTo(map(bird));
    assertThat(index.apply("water")).isEqualTo(map(cat, dog, cow, sheep, bird));
  }

  @Test
  public void indexingBySingleValueAttributeIsPossible() {
    IndexedMap<Id, Animal> map = mapWithAnimals();

    Function<Integer, Map<Id, Animal>> index =
          map.addIndex((id, a) -> singleton(a.legs));

    assertThat(index.apply(0)).isEqualTo(map());
    assertThat(index.apply(2)).isEqualTo(map(bird));
    assertThat(index.apply(4)).isEqualTo(map(cat, dog, cow, sheep));
  }

  @Test
  public void whenRemovingAnimalsTheIndexShouldBeUpdated() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    map.insert(cat.id, cat);
    map.insert(dog.id, dog);
    Function<Integer, Map<Id, Animal>> index = map.addIndex((id, a) -> singleton(a.legs));
    map.delete(dog.id);
    map.delete(cat.id);

    assertThat(index.apply(4)).isSameAs(Collections.<Id, Animal>emptyMap());
  }

  @Test
  public void whenRemovingLastIndexedValueIndexShouldIndicateNoAnimals() {
    IndexedMap<Id, Animal> map = mapWithAnimals();
    Function<String, Map<Id, Animal>> index = map.addIndex((id, a) -> a.foods);

    map.delete(cat.id);
    map.delete(dog.id);

    assertThat(index.apply("biscuits")).isEqualTo(map());
  }

  @Test
  public void insertingNewAnimalShouldUpdateIndex() {
    IndexedMap<Id, Animal> map = mapWithAnimals();
    Function<String, Map<Id, Animal>> index = map.addIndex((id, a) -> a.foods);

    map.insert(dog.id, woundedDog);

    assertThat(index.apply("biscuits")).isEqualTo(map(cat));
    assertThat(index.apply("medicine")).isEqualTo(map(woundedDog));
  }

  @Test
  public void dynamicallyAddedIndexShouldSeePreexistingValues() {
    IndexedMap<Id, Animal> map = mapWithAnimals();

    Function<Integer, Map<Id, Animal>> index =
          map.addIndex((id, a) -> singleton(a.legs));

    assertThat(
          new ArrayList<>(index.apply(4).values())).isEqualTo(
          Arrays.asList(dog, cat, cow, sheep));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void doNotAllowExternalModificationOfIndexMap() {
    IndexedMap<Id, Animal> map = mapWithAnimals();
    Function<String, Map<Id, Animal>> index =
          map.addIndex((id, a) -> a.foods);
    Map<Id, Animal> indexed = index.apply("biscuits");

    indexed.remove(cat.id);
  }


  private IndexedMap<Id, Animal> mapWithAnimals() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);
    map.insert(cat.id, cat);
    map.insert(cow.id, cow);
    map.insert(sheep.id, sheep);
    map.insert(bird.id, bird);
    return map;
  }

}
