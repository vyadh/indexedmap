package org.softpres.animal;

import java.util.Set;

/**
 * Simple animal class to test {@link org.softpres.indexedmap.IndexedMap}.
 */
public class Animal {

  public final Id id;
  public final String name;
  public final int legs;
  public final Set<String> foods;

  public Animal(Id id, String name, int legs, Set<String> foods) {
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
