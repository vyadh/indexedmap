/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.softpres.indexedmap.Animals.*;

/**
 * Unit tests for basic operations on {@link org.softpres.indexedmap.IndexedHashMap}.
 */
public class IndexedMapTest {

  @Test
  public void sizeIsZeroWhenEmpty() throws Exception {
    assertThat(new IndexedHashMap<Id, Animal>().size(), is(0));
  }

  @Test
  public void sizeReflectsItemsInTheMap() throws Exception {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);
    map.insert(cat.id, cat);

    assertThat(map.size(), is(2));
  }

  @Test
  public void isEmptyIsTrueWithNoEntries() throws Exception {
    assertThat(new IndexedHashMap<Id, Animal>().isEmpty(), is(true));
  }

  @Test
  public void isEmptyIsFalseWithoutEntries() throws Exception {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);

    assertThat(map.isEmpty(), is(false));
  }

  @Test
  public void containsKeysWhenInTheMap() throws Exception {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(dog.id, dog);

    assertThat(map.containsKey(cat.id), is(false));
    assertThat(map.containsKey(dog.id), is(true));
  }

  @Test
  public void containsValuesWhenInTheMap() throws Exception {
    IndexedHashMap<Id, Animal> map = new IndexedHashMap<>();
    map.insert(cat.id, cat);

    assertThat(map.containsValue(dog), is(false));
    assertThat(map.containsValue(cat), is(true));
  }

  @Test
  public void removeUsingObjectTypeThatIsNotValidKeyTypeReturnsNull() {
    IndexedMap<Id, Animal> map = new IndexedHashMap<>();

    Animal previous = map.remove(new Object());

    assertThat(previous, nullValue());
  }

  @Test
  public void putAllJustCallsPutForEveryEntry() throws Exception {
    List<Id> putKeys = new ArrayList<>();
    List<Animal> putValues = new ArrayList<>();

    IndexedMap<Id, Animal> map = new IndexedHashMap<Id, Animal>() {
      public Animal put(Id key, Animal value) {
        putKeys.add(key);
        putValues.add(value);
        return super.put(key, value);
      }
    };

    map.putAll(map(dog, cat, fish));

    assertThat(putKeys, is(Arrays.asList(dog.id, cat.id, fish.id)));
    assertThat(putValues, is(Arrays.asList(dog, cat, fish)));
  }

  @Test
  @Ignore // Currently if we create this weird situation, it doesn't even fail atomically
  public void incompatibleObjectMatchingExistingKeyIsIgnoredAndDoesNotThrowCCE() {
    class PreviousKey extends SillyEquality { }
    class AlternateKey extends SillyEquality { }
    IndexedMap<PreviousKey, Integer> map = new IndexedHashMap<>();
    map.addIndex((k, v) -> Collections.emptyList());

    map.insert(new PreviousKey(), 1);
    Integer previous = map.remove(new AlternateKey());

    assertThat(previous, nullValue());
    assertThat(map.get(new PreviousKey()), is(1));
  }

  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  class SillyEquality {
    @Override
    public boolean equals(Object obj) {
      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }
  }

}
