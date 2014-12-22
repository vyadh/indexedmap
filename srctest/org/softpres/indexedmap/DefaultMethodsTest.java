/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Simple tests for Map's default methods. These simply verify the methods
 * still work, concurrency correctness testing is attempted in the class
 * {@link LockingThreadedTest}.
 */
public class DefaultMethodsTest {

  private final IndexedMap<String, Integer> map =
        new IndexedMapBuilder<String, Integer>().build();

  @Test
  public void getOrDefaultSimpleTest() {
    map.put("one", 1);

    assertThat(map.getOrDefault("zero", 0), is(0));
    assertThat(map.getOrDefault("one", 0), is(1));
  }

  @Test
  public void forEachSimpleTest() {
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);

    Set<SimpleImmutableEntry<String, Integer>> entries = new HashSet<>();
    map.forEach((k, v) -> entries.add(new SimpleImmutableEntry<>(k, v)));

    assertThat(entries, is(new HashSet<>(Arrays.asList(
          new SimpleImmutableEntry<>("one", 1),
          new SimpleImmutableEntry<>("two", 2),
          new SimpleImmutableEntry<>("three", 3)
    ))));
  }

  @Test
  public void replaceAllUpdatesValues() {
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);

    map.replaceAll((k, v) -> v + 1);

    assertThat(map.entrySet(), is(new HashSet<>(Arrays.asList(
          new SimpleImmutableEntry<>("one", 2),
          new SimpleImmutableEntry<>("two", 3),
          new SimpleImmutableEntry<>("three", 4)
    ))));
  }

  @Test
  public void replaceAllUpdatesIndexes() {
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);
    map.put("four", 4);
    Function<Boolean, Map<String, Integer>> evenOrOdd =
          map.addIndex((k1, v1) -> Collections.singleton(v1 % 2 == 0));

    map.replaceAll((k, v) -> v + 1);

    Set<Integer> evens = values(evenOrOdd.apply(true));
    Set<Integer> odds = values(evenOrOdd.apply(false));

    assertThat(evens, is(set(2, 4)));
    assertThat(odds, is(set(3, 5)));
  }

  @Test
  public void removeUpdatesValues() {
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);
    Function<Boolean, Map<String, Integer>> evenOrOdd =
          map.addIndex((k1, v1) -> Collections.singleton(v1 % 2 == 0));

    map.remove("one", 1);

    assertThat(values(evenOrOdd.apply(true)), is(set(2)));
    assertThat(values(evenOrOdd.apply(false)), is(set(3)));
  }

  @Test
  public void removeUpdatesIndexes() {
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);

    map.remove("one", 2);
    assertThat(map.get("one"), is(1));

    map.remove("one", 1);
    assertThat(map.containsKey("one"), is(false));
  }

  // todo test compute
  // todo test computeIfAbsent
  // todo test computeIfPresent
  // todo test putIfAbsent
  // todo test remove
  // todo test replace * 2
  // todo test merge

  private Set<Integer> values(Map<String, Integer> entries) {
    return new HashSet<>(entries.values());
  }

  private Set<Integer> set(Integer... items) {
    return new HashSet<>(Arrays.asList(items));
  }

}