/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * Perform a similar set of random operations as ScalaSTM does,
 * but in a single thread.
 */
public class IndexedMapBench {

  private final IndexedMap<Integer, User> users = new IndexedMap<>();
  private final Function<String, Map<Integer, User>> byName = users.addIndex((id, u) -> singleton(u.name));
  private final Function<String, Map<Integer, User>> byLike = users.addIndex((id, u) -> u.likes);

  private final String[] topBabyNames = {
        "Ethan", "Isabella", "Jacob", "Olivia", "Noah", "Sophia", "Logan", "Emma", "Liam", "Ava", "Aiden", "Abigail",
        "Mason", "Chloe", "Jackson", "Madison", "Jack", "Ella", "Jayden", "Addison", "Ryan", "Emily", "Matthew", "Lily",
        "Lucas", "Mia", "Michael", "Avery", "Alexander", "Grace", "Nathan", "Hannah" };
  private final String[] languages = { "scala", "java", "C++", "haskell", "clojure", "python", "ruby", "pascal", "perl" };
  private final String[] sports = { "climbing", "cycling", "hiking", "football", "baseball", "underwater hockey" };

  private final Random rand = new Random();
  private final int numIDs = 1000;

  private String pick(String[] a) {
    return a[rand.nextInt(a.length)];
  }

  private void newName(Integer id) {
    User before = users.get(id).orElse(new User(id, "John Doe", emptySet()));
    User after = new User(id, pick(topBabyNames), before.likes);
    users.put(id, after);
  }

  private void newLikes(Integer id) {
    User before = users.get(id).orElse(new User(id, "John Doe", emptySet()));
    User after = new User(id, before.name, new HashSet<>(Arrays.asList(pick(languages), pick(sports))));
    users.put(id, after);
  }

  private void randomOp() {
    int percent = rand.nextInt(100);
    if (percent < 10) {
      users.get(rand.nextInt(numIDs));
    }
    else if (percent < 40) {
      byName.apply(pick(topBabyNames));
    } else if (percent < 60) {
      byLike.apply(pick(sports));
    } else if (percent < 80) {
      byLike.apply(pick(languages));
    } else if (percent < 90) {
      newName(rand.nextInt(numIDs));
    } else {
      newLikes(rand.nextInt(numIDs));
    }
  }

  private void populate() {
    for (int id = 0; id < numIDs; id++) {
      newName(id);
      newLikes(id);
    }
  }

  private long run(int ops) {
    long begin = System.currentTimeMillis();

    for (int i = 0; i < ops; i++) {
      randomOp();
    }

    long end = System.currentTimeMillis();
    return end - begin;
  }

  public static void main(String[] args) {
    IndexedMapBench bench = new IndexedMapBench();
    bench.populate();

    for (int pass = 0; pass < 10; pass++) {
      long elapsed = bench.run(10_000_000);
      System.out.printf("%4.2f usec/op total throughput (80%% read)\n", elapsed * 0.001);
    }
  }

  private static class User {
    private final Integer id;
    private final String name;
    private final Set<String> likes;

    public User(Integer id, String name, Set<String> likes) {
      this.id = id;
      this.name = name;
      this.likes = likes;
    }
  }

}
