package org.softpres.indexedmap;

import java.util.Set;

/**
 * Simple animal class to test {@link IndexedMap}.
 */
class Animal {

  final Id id;
  final String name;
  final int legs;
  final Set<String> foods;

  Animal(Id id, String name, int legs, Set<String> foods) {
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
