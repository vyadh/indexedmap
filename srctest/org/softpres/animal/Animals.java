package org.softpres.animal;

import java.util.*;

/**
 * Helper animal methods for tests.
 */
public class Animals {

  public static final Animal dog = new Animal(new Id(1), "Dog", 4, foods("rabbit", "biscuits", "water"));
  public static final Animal cat = new Animal(new Id(2), "Cat", 4, foods("fish", "mouse", "biscuits", "water"));
  public static final Animal cow = new Animal(new Id(3), "Cow", 4, foods("grass", "water"));
  public static final Animal sheep = new Animal(new Id(4), "Sheep", 4, foods("grass", "water"));
  public static final Animal bird = new Animal(new Id(5), "Bird", 2, foods("worm", "water"));
  public static final Animal fish = new Animal(new Id(6), "Fish", 0, foods("plankton", "water"));

  public static final Animal woundedDog = new Animal(new Id(1), "Dog", 3, foods("medicine"));

  public static Map<Id, Animal> map(Animal... animals) {
    HashMap<Id, Animal> result = new LinkedHashMap<>();
    for (Animal animal : animals) {
      result.put(animal.id, animal);
    }
    return result;
  }

  public static HashSet<String> foods(String... foods) {
    return new HashSet<>(Arrays.asList(foods));
  }

}
