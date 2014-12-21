/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Concurrency tests for map operations.
 */
public class LockingThreadedTest {

  private final int n = 15_000;

  private final IndexedMap<String, Integer> map = new IndexedMapBuilder<String, Integer>().build();
  private final Function<Boolean, Map<String, Integer>> byEven = map.addIndex(this::even);
  private final Function<Boolean, Map<String, Integer>> byOdd = map.addIndex(this::odd);

  @SuppressWarnings("Convert2MethodRef") // Symmetry
  private final List<Consumer<String>> ops = Arrays.<Consumer<String>>asList(
        key -> map.select(key),
        key -> map.insert(key, map.getOrDefault(key, 0) + 1),
        key -> map.delete(key + 10),
        key -> map.computeIfPresent(key, (k, v) -> v + 11),
        key -> map.put(key, map.containsKey(key) ? 1 : 2),
        key -> map.put(key, map.containsValue(10) ? 3 : 4),
        key -> map.computeIfPresent(key, (k, v) -> v + 22),
        key -> map.computeIfPresent(key, (k, v) -> v + 33),
        key -> map.getOrDefault(key, 0),
        key -> map.forEach((k, v) -> { }),
        key -> map.replaceAll((k, v) -> v), //todo modify
        key -> map.putIfAbsent(key + 1, 42),
        key -> map.replace(key, 1, 2),
        key -> map.replace(key, 42),
        key -> map.computeIfAbsent(key + 2, k -> n),
        key -> map.compute(key, (k, v) -> v == null ? 0 : v / 2),
        key -> map.merge(key, 8, (ov, nv) -> ov + nv)
  );

  @Test
  public void concurrencyCheckWhenNumbersAreFromRunningSerially() throws InterruptedException {
    Processor p1 = new Processor("one", 22);
    Processor p2 = new Processor("two", 44);
    Processor p3 = new Processor("three", 66);
    Processor p4 = new Processor("four", 88);

    // Note that when in parallel, it works slower because of lock contention
    boolean parallel = true;

    if (parallel) {
      p1.start();
      p2.start();
      p3.start();
      p4.start();
      p1.join();
      p2.join();
      p3.join();
      p4.join();
    } else {
      p1.start();
      p1.join();
      p2.start();
      p2.join();
      p3.start();
      p3.join();
      p4.start();
      p4.join();
    }

    assertThat(
          sum(map),
          is(450090267)
    );

    assertThat(sum(byEven.apply(Boolean.TRUE)), is(225090210));
    assertThat(sum(byOdd.apply(Boolean.FALSE)), is(225090210));
    assertThat(sum(byOdd.apply(Boolean.TRUE)),   is(225000057));
    assertThat(sum(byEven.apply(Boolean.FALSE)), is(225000057));
  }

  private int sum(Map<String, Integer> m) {
    return m.values().stream().mapToInt(v -> v).sum();
  }

  class Processor extends Thread {
    private final String name;
    private final int seed;

    Processor(String name, int seed) {
      this.name = name;
      this.seed = seed;
    }

    @Override
    public void run() {
      process(name, seed);
    }
  }

  private void process(String key, int seed) {
    populateMap(key);

    Random rand = new Random(seed);

    for (int i=0; i< n; i++) {
      int opIndex = rand.nextInt(ops.size());
      Consumer<String> op = ops.get(opIndex);
      op.accept(key);
    }
  }

  private void populateMap(String key) {
    map.put(key, 0);
    for (int i = 1; i <= n; i++) {
      map.put(key + '_' + i, i);
    }
  }

  private List<Boolean> even(String key, Integer value) {
    return Collections.singletonList(value % 2 == 0);
  }

  private List<Boolean> odd(String key, Integer value) {
    return  Collections.singletonList(value % 2 != 0);
  }

}
