/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static org.softpres.indexedmap.Animals.*;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link IndexedHashMap}.
 */
public class IndexedMapTest {

  @Test
  public void insertReplacesPreviousItem() {
    IndexedMap<Integer, Animal> map = new IndexedHashMap<>();

    map.insert(42, cat);
    Optional<Animal> previous = map.insert(42, dog);

    assertThat(previous, is(Optional.of(cat)));
  }

  @Test
  public void selectingItemThatHasBeenPreviouslyInserted() {
    IndexedMap<Integer, Animal> map = new IndexedHashMap<>();

    map.insert(42, dog);

    assertThat(map.select(42), is(Optional.of(dog)));
  }

  @Test
  public void insertNewItemReturnsEmptyOptional() {
    IndexedMap<Integer, Animal> map = new IndexedHashMap<>();

    Optional<Animal> previous = map.insert(42, dog);

    assertThat(previous, is((Optional.empty())));
  }

  @Test
  public void deleteWithoutPreviousItemReturnsEmpty() {
    IndexedMap<Integer, Animal> map = new IndexedHashMap<>();

    Optional<Animal> previous = map.delete(42);

    assertThat(previous, is(Optional.empty()));
  }

  @Test
  public void deleteWithPreviousItemReturnsPrevious() {
    IndexedMap<Integer, Animal> map = new IndexedHashMap<>();

    map.insert(53, dog);
    Optional<Animal> previous = map.delete(53);

    assertThat(previous, is((Optional.of(dog))));
  }

  @Test
  public void indexByUnknownFoodReturnsNoAnimals() {
    IndexedMap<Integer, Animal> map = mapWithAnimals();

    Function<String, Map<Integer, Animal>> index =
          map.addIndex((id, a) -> a.foods);

    assertThat(index.apply("sausage"), equalTo(map()));
  }

  @Test
  public void animalsAreIndexedByDifferentFoods() {
    IndexedMap<Integer, Animal> map = mapWithAnimals();

    Function<String, Map<Integer, Animal>> index =
          map.addIndex((id, a) -> a.foods);

    assertThat(index.apply("biscuits"), equalTo(map(cat, dog)));
    assertThat(index.apply("fish"), equalTo(map(cat)));
    assertThat(index.apply("mouse"), equalTo(map(cat)));
    assertThat(index.apply("rabbit"), equalTo(map(dog)));
    assertThat(index.apply("grass"), equalTo(map(cow, sheep)));
    assertThat(index.apply("worm"), equalTo(map(bird)));
    assertThat(index.apply("water"), equalTo(map(cat, dog, cow, sheep, bird)));
  }

  @Test
  public void indexingBySingleValueAttributeIsPossible() {
    IndexedMap<Integer, Animal> map = mapWithAnimals();

    Function<Integer, Map<Integer, Animal>> index =
          map.addIndex((id, a) -> singleton(a.legs));

    assertThat(index.apply(0), equalTo(map()));
    assertThat(index.apply(2), equalTo(map(bird)));
    assertThat(index.apply(4), equalTo(map(cat, dog, cow, sheep)));
  }

  @Test
  public void whenRemovingAnimalsTheIndexShouldBeUpdated() {
    IndexedMap<Integer, Animal> map = new IndexedHashMap<>();

    map.insert(cat.id, cat);
    map.insert(dog.id, dog);
    Function<Integer, Map<Integer, Animal>> index = map.addIndex((id, a) -> singleton(a.legs));
    map.delete(dog.id);
    map.delete(cat.id);

    assertThat(index.apply(4), sameInstance(Collections.<Integer, Animal>emptyMap()));
  }

  @Test
  public void whenRemovingLastIndexedValueIndexShouldIndicateNoAnimals() {
    IndexedMap<Integer, Animal> map = mapWithAnimals();
    Function<String, Map<Integer, Animal>> index = map.addIndex((id, a) -> a.foods);

    map.delete(cat.id);
    map.delete(dog.id);

    assertThat(index.apply("biscuits"), equalTo(map()));
  }

  @Test
  public void insertingNewAnimalShouldUpdateIndex() {
    IndexedMap<Integer, Animal> map = mapWithAnimals();
    Function<String, Map<Integer, Animal>> index = map.addIndex((id, a) -> a.foods);

    map.insert(dog.id, woundedDog);

    assertThat(index.apply("biscuits"), equalTo(map(cat)));
    assertThat(index.apply("medicine"), equalTo(map(woundedDog)));
  }

  @Test
  public void dynamicallyAddedIndexShouldSeePreexistingValues() {
    IndexedMap<Integer, Animal> map = mapWithAnimals();

    Function<Integer, Map<Integer, Animal>> index =
          map.addIndex((id, a) -> singleton(a.legs));

    assertThat(
          new ArrayList<>(index.apply(4).values()),
          equalTo(Arrays.asList(dog, cat, cow, sheep)));
  }


  private IndexedMap<Integer, Animal> mapWithAnimals() {
    IndexedMap<Integer, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);
    map.insert(cat.id, cat);
    map.insert(cow.id, cow);
    map.insert(sheep.id, sheep);
    map.insert(bird.id, bird);
    return map;
  }

}
