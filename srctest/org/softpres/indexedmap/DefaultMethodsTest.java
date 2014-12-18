/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
  public void getOrDefaultUsesMapsDefaultMethod() {
    map.put("one", 1);

    assertThat(map.getOrDefault("zero", 0), is(0));
    assertThat(map.getOrDefault("one", 0), is(1));
  }

  @Test
  public void forEachUsesMapsDefaultMethod() {
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

  // todo test replaceAll
  // todo test putIfAbsent
  // todo test remove
  // todo test replace * 2
  // todo test computeIfAbsent
  // todo test computeIfPresent
  // todo test compute
  // todo test merge

}
