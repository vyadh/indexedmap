Indexed Map
===========

An in-memory map that works a little like a database table with indices. A
secondary index can be added on demand to provide fast lookups by providing
the indexing strategy via a simple Java 8 lambda expression.

This is inspired by [ScalaSTM][1]'s `IndexedMap`, but for single-threaded usage.
It could also serve as a migration path towards ScalaSTM if multi-thread usage is
later required.

The intended use case is for managing state for applications that have
a fast, single threaded event processor for queries and updates.

Dependencies
------------

IndexedMap has no runtime dependencies other than Java 8.

Usage
-----

This library contains a simplified Map-like interface IndexedMap. It is not
intended to be used as a general-purpose map, but may very well be extended
to implement all of `java.util.Map` in the future.

To allow this to happen, alternative methods making use of `Optional` are
provided, as future siblings to the null-using `java.util.Map` versions.

* `insert` is the alternative to `put`
* `select` is the alternative to `get`
* `delete` is the alternative to `remove`

Example
-------

```java
Animal dog = new Animal("Dog").withFoods("rabbit", "biscuits");
Animal cat = new Animal("Cat").withFoods("fish", "biscuits");
Animal fish = new Animal("Fish").withFoods("plankton");

IndexedMap<Integer, Animal> map = new IndexedMap<>();
map.insert(1, dog);
map.insert(2, cat);
map.insert(3, fish);

// Secondary index definition
Function<Food, Map<Integer, Animal>> indexByFood =
  map.addIndex((id, animal) -> animal.foods());

// Index lookup
Collection<Animal> biscuitEaters = indexByFood.apply(food("biscuits"));

assertEquals(
  Sets.newHashSet(dog, cat)),
  Sets.newHashSet(biscuitEaters)
);
```

Implementation
--------------

There is currently one implementation `IndexedHashMap`, where the primary
index, and added secondary indexes are backed by a normal `java.util.HashMap`.

Mutable Values
--------------

Using mutable values with IndexedMap is best avoided. It is better used with
immutable values only. However, sometimes there is little choice in the
mutability of types being used because of application design constraints,
particularly in the Java world with the common JavaBean standard.

Here is one possible strategy to deal with situations where immutable types
are inconvenient given an application's architecture, and converting between
mutable and immutable types for the `IndexedMap` is not desirable.

1. Create an interface or wrapper object that either hides the non-mutating
   functionality (more type safe), or throws exceptions when it is attempted
   (less type safe, but perhaps more convenient when enough unit tests are
   available to catch any infraction).
2. Add some sort of `mutate()` method to the above type, which will copy and
   return an appropriate object for mutation.
3. Push the changed mutable type back into the map when finished, ensuring
   there are no other references or code to execute that can mutate it further.

Another strategy is to just rely on application code being careful about
copying objects before mutating. This is a recipe for problems however, as it
is not obvious when it happens, and it may be difficult to track down problems.

It is difficult to provide functionality that helps support mutable values
without being opinionated about the implementation strategy, and without
affecting performance for reasons an application may not need anyway. So no
such functionality is included.

[1]: http://nbronson.github.io/scala-stm
