Indexed Map
===========

An in-memory map that works a little like a database table with indices. A
secondary index can be added on demand to provide fast lookups by providing
the indexing strategy via a simple Java 8 lambda expression.

This is inspired by [ScalaSTM][1]'s `IndexedMap`, but most suitable for single
single-threaded usage. It could also serve as a migration path towards ScalaSTM
if multi-thread usage is later required (ScalaSTM provides an alternative
Java-friendly API). It is possible to use IndexedMap in a multi-threaded context,
but this does not have any built-in transaction isolation capability, and is also
not very efficient given a `ReadWriteLock` is used across the whole map to ensure
the indexes are always maintained.

The intended use-case is to manage state for applications that have a fast,
single threaded event processor for queries and updates. Practically this will
usually mean a workload where operations are short-lived, or where long-running
operations can be run separately to the state management.

Dependencies
------------

IndexedMap has no runtime dependencies other than Java 8.

Usage
-----

This library contains a simplified Map-like interface IndexedMap. It is not
intended to be used as a general-purpose map, but implements `java.util.Map`
for convenience.

Alternative methods making use of `Optional` are provided, as siblings to the
null-using `java.util.Map` versions.

* `insert` is the alternative to `put`
* `select` is the alternative to `get`
* `delete` is the alternative to `remove`

Example
-------

```java
Animal dog = new Animal("Dog").withFoods("rabbit", "biscuits");
Animal cat = new Animal("Cat").withFoods("fish", "biscuits");
Animal fish = new Animal("Fish").withFoods("plankton");

IndexedMap<Integer, Animal> map = new IndexedMapBuilder<>().build();

map.insert(1, dog);
map.insert(2, cat);
map.insert(3, fish);

// Secondary index definition
Function<Food, Map<Integer, Animal>> indexByFood =
  map.addIndex((id, animal) -> animal.foods());

// Index lookup
Map<Integer, Animal> biscuitEaters = indexByFood.apply(new Food("biscuits"));

assertEquals(
  Sets.newHashSet(dog, cat)),
  Sets.newHashSet(biscuitEaters.values())
);
```

Implementation
--------------

There is currently one implementation `IndexedHashMap`, where the primary
index, and secondary indexes are backed by a normal `java.util.HashMap`
instances.

Concurrency
-----------

By default, locking of map operations is performed using a `ReadWriteLock`.
If running operations a single thread, this can be turned off in the builder:

```java
IndexedMap<Integer, Animal> map = new IndexedMapBuilder<>()
      .lockingStrategy(new NoReadWriteLock())
      .build();
```

Java 8 default methods for `Map` are also supported.

Optimistic Locking
------------------

An optional optimistic locking facility is provided to protect the time between
reads and writes of values. This works by checking writes for values are for
the same 'revision' as the ones read. If the change is permitted, the revision
is incremented and placed back into the map.

This feature is currently a work in progress.

Transactions
------------

One of the great things about [ScalaSTM][1] is that it provides serialisable
transaction isolation guarantees. A ScalaSTM `TMap` can be used with an
`IndexedMap`, and they naturally compose operations into single atomic commits.

The `IndexedMap` implemented here provides no such guarantees. It may be possible
to provide features to allow map operations to be staged until a commit phase,
but this is non-trivial because we would also want to stage the secondary index
updates (achieve snapshot MVCC).

One workable strategy would be to design an application so that updates to the
map all happen at the end of each operation. This would also fit in with any IO
that needs to take place. The general idea being that all "risky" operations
such as application processing and IO is done first, and the updates to the map
(not likely to fail) can happen if everything else went well. This, in many
applications, is likely good enough. For more advance requirements, there is
ScalaSTM.

Here is an example of how this might work.

```java
interface ChangeCollector<K, V> {
  void insert(K key, V value);
  void delete(K key);
}

interface Operation<K, V> {
  void operate(IndexedMap<K, V> map, ChangeCollector<K, V> collector);
}

class ExchangeAnimalOperation implements Operation<Integer, Animal> {
  private final Integer sellingId;
  private final Animal buying;

  ExchangeAnimalOperation(Integer sellingId, Animal buying) {
    this.sellingId = sellingId;
    this.buying = buying;
  }

  public void operate(
        IndexedMap<Integer, Animal> map,
        ChangeCollector<Integer, Animal> collector) {

    Optional<Animal> selling = map.select(sellingId);
    if (selling.isPresent()) {
      collector.delete(sellingId);
      applicationProcessingThatMayFail();
      collector.insert(buying.id, buying);
    }
  }
}
```

The collected changes can then be played back on the `IndexedMap` at a time
where there is no more unmanaged code to execute that may legitimately fail.
Something that could be combined with a write-ahead persistence strategy for
example, to complete the IO operation, and only then allow the collected
changes to be applied.

It puts more burden on the application programmer, but it might be good enough,
and perhaps a few steps closer towards a ScalaSTM future.

Mutable Values
--------------

Using mutable values with IndexedMap is best avoided. It is recommended that
only immutable values are used only. However, sometimes there is little choice
in the mutability of types being used because of application design constraints,
particularly in the Java world with the common JavaBean standard.

Here is one strategy to deal with situations where immutable types are inconvenient
given an application's architecture, and converting between mutable and immutable
types for the `IndexedMap` is not desirable.

1. Create an interface or wrapper object that either hides the non-mutating
   functionality (more type safe), or throws exceptions when it is attempted
   (less type safe, but perhaps more practical when enough unit tests are
   available to catch any infractions).
2. Add some sort of `mutate()` method to the above type, which will copy and
   return an appropriate object for mutation.
3. Push the changed mutable type back into the map when finished, ensuring
   there are no other references or code to execute that can mutate it further.

Another strategy is to rely on application code being careful about copying
objects before mutating. This is a recipe for problems however, as it is not
obvious when it happens, and it may be difficult to track down problems.

It is difficult to provide functionality that helps support mutable values
without being opinionated about the implementation strategy, and without
affecting performance for reasons other applications may not need anyway.
For these reasons, such functionality is not included.

[1]: http://nbronson.github.io/scala-stm
