package org.softpres.indexedmap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Helper animal methods for tests.
 */
class Animals {

  static final Animal dog = new Animal(1, "Dog", 4, foods("rabbit", "biscuits", "water"));
  static final Animal cat = new Animal(2, "Cat", 4, foods("fish", "mouse", "biscuits", "water"));
  static final Animal cow = new Animal(3, "Cow", 4, foods("grass", "water"));
  static final Animal sheep = new Animal(4, "Sheep", 4, foods("grass", "water"));
  static final Animal bird = new Animal(5, "Bird", 2, foods("worm", "water"));
  static final Animal fish = new Animal(6, "Fish", 0, foods("plankton", "water"));

  static final Animal woundedDog = new Animal(1, "Dog", 3, foods("medicine"));

  static HashMap<Integer, Animal> map(Animal... animals) {
    HashMap<Integer, Animal> result = new HashMap<>();
    for (Animal animal : animals) {
      result.put(animal.id, animal);
    }
    return result;
  }


  static HashSet<String> foods(String... foods) {
    return new HashSet<>(Arrays.asList(foods));
  }

}
