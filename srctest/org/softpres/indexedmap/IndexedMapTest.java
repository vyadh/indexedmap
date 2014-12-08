/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link IndexedMap}.
 */
public class IndexedMapTest {

  class Animal {
    final int id;
    final String name;
    final int legs;
    final Set<String> foods;

    Animal(int id, String name, int legs, Set<String> foods) {
      this.id = id;
      this.name = name;
      this.legs = legs;
      this.foods = foods;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  Animal dog = new Animal(1, "Dog", 4, foods("rabbit", "biscuits", "water"));
  Animal cat = new Animal(2, "Cat", 4, foods("fish", "mouse", "biscuits", "water"));
  Animal cow = new Animal(3, "Cow", 4, foods("grass", "water"));
  Animal sheep = new Animal(4, "Sheep", 4, foods("grass", "water"));
  Animal bird = new Animal(5, "Bird", 2, foods("worm", "water"));
//  Animal fish = new Animal(6, "Fish", 0, foods("plankton", "water"));

  Animal woundedDog = new Animal(1, "Dog", 3, foods("medicine"));

  IndexedMap<Integer, Animal> map;

  @Before
  public void init() {
    map = new IndexedMap<>();
  }

  @Test
  public void putReplacesPreviousItem() {
    map.put(42, cat);
    Optional<Animal> previous = map.put(42, dog);
    assertThat(previous, is(Optional.of(cat)));
  }

  @Test
  public void gettingItemThatHasBeenPreviouslyPut() {
    map.put(42, dog);
    assertThat(map.get(42), is(Optional.of(dog)));
  }

  @Test
  public void putNewItemReturnsEmptyOptional() {
    Optional<Animal> previous = map.put(42, dog);
    assertThat(previous, is((Optional.empty())));
  }

  @Test
  public void removeWithoutPreviousItemReturnsEmpty() {
    Optional<Animal> previous = map.remove(42);
    assertThat(previous, is(Optional.empty()));
  }

  @Test
  public void removeWithPreviousItemReturnsPrevious() {
    map.put(53, dog);
    Optional<Animal> previous = map.remove(53);
    assertThat(previous, is((Optional.of(dog))));
  }

  @Test
  public void indexByUnknownFoodReturnsNoAnimals() {
    Function<String, Map<Integer, Animal>> index = indexAnimalsBy((id, a) -> a.foods);

    assertThat(index.apply("sausage"), equalTo(map()));
  }

  @Test
  public void animalsAreIndexedByDifferentFoods() {
    Function<String, Map<Integer, Animal>> index = indexAnimalsBy((id, a) -> a.foods);

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
    Function<Integer, Map<Integer, Animal>> index =
          indexAnimalsBy((id, a) -> singleton(a.legs));

    assertThat(index.apply(0), equalTo(map()));
    assertThat(index.apply(2), equalTo(map(bird)));
    assertThat(index.apply(4), equalTo(map(cat, dog, cow, sheep)));
  }

  @Test
  public void whenRemovingAnimalsTheIndexShouldBeUpdated() {
    map.put(cat.id, cat);
    map.put(dog.id, dog);
    Function<Integer, Map<Integer, Animal>> index = map.addIndex((id, a) -> singleton(a.legs));
    map.remove(dog.id);
    map.remove(cat.id);

    assertThat(index.apply(4), sameInstance(Collections.<Integer, Animal>emptyMap()));
  }

  @Test
  public void whenRemovingLastIndexedValueIndexShouldIndicateNoAnimals() {
    Function<String, Map<Integer, Animal>> index = indexAnimalsBy((id, a) -> a.foods);
    map.remove(cat.id);
    map.remove(dog.id);

    assertThat(index.apply("biscuits"), equalTo(map()));
  }

  @Test
  public void puttingNewAnimalShouldUpdateIndex() {
    Function<String, Map<Integer, Animal>> index = indexAnimalsBy((id, a) -> a.foods);
    map.put(dog.id, woundedDog);

    assertThat(index.apply("biscuits"), equalTo(map(cat)));
    assertThat(index.apply("medicine"), equalTo(map(woundedDog)));
  }

  @Test
  public void dynamicallyAddedIndexShouldSeePreexistingValues() {
    putAllAnimals();

    Function<Integer, Map<Integer, Animal>> index = map.addIndex((id, a) -> singleton(a.legs));

    assertThat(
          new ArrayList<>(index.apply(4).values()),
          equalTo(Arrays.asList(dog, cat, cow, sheep)));
  }



  private HashSet<String> foods(String... foods) {
    return new HashSet<>(Arrays.asList(foods));
  }

  private <T> Function<T, Map<Integer, Animal>> indexAnimalsBy(
        BiFunction<Integer, Animal, Iterable<T>> f) {

    putAllAnimals();
    return map.addIndex(f);
  }

  private HashMap<Integer, Animal> map(Animal... animals) {
    HashMap<Integer, Animal> result = new HashMap<>();
    for (Animal animal : animals) {
      result.put(animal.id, animal);
    }
    return result;
  }

  private void putAllAnimals() {
    map.put(dog.id, dog);
    map.put(cat.id, cat);
    map.put(cow.id, cow);
    map.put(sheep.id, sheep);
    map.put(bird.id, bird);
  }

}
