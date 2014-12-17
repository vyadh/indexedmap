/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Simple tests for Map's default methods. These simply verify the methods
 * still work, concurrency correctness testing is attempted in the class
 * {@link LockingThreadedTest}.
 */
public class LockingDefaultMethodsTest {

  private final IndexedMap<String, Integer> map =
        new IndexedMapBuilder<String, Integer>().build();

  @Test
  public void getOrDefaultUsesMapsDefaultMethod() {
    map.put("one", 1);

    assertThat(map.getOrDefault("zero", 0), is(0));
    assertThat(map.getOrDefault("one", 0), is(1));
  }

  // todo test forEach
  // todo test replaceAll
  // todo test putIfAbsent
  // todo test remove
  // todo test replace * 2
  // todo test computeIfAbsent
  // todo test computeIfPresent
  // todo test compute
  // todo test merge

}
