/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ManagedMap}.
 */
public class ManagedWriteTest {

  private final CaptureAdd onNew = new CaptureAdd();
  private final CaptureChange onChange = new CaptureChange();
  private final CaptureDelete onDelete = new CaptureDelete();
  private final Map<String, Integer> map = new ManagedMap<>(
        new HashMap<>(), onNew, onChange, onDelete);

  @Test
  public void clearCallsOnDeleteForAllValues() {
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);

    map.clear();

    assertThat(onDelete.allDeleted).isEqualTo(Arrays.asList(1, 2, 3));
  }

  @Test
  public void putCallsOnAddWhenNoPreviousValue() {
    map.put("value", 1);

    assertThat(onNew.added).isEqualTo(1);
  }

  @Test
  public void putCallsOnChangeWhenPreviousValueExists() {
    map.put("value", 1);
    map.put("value", 2);

    assertThat(onChange.current).isEqualTo(1);
    assertThat(onChange.replacement).isEqualTo(2);
  }

  @Test
  public void removeDoesNotCallOnDeleteIfKeyDidNotExist() {
    map.remove("missing");

    assertThat(onDelete.allDeleted).isEmpty();
  }

  @Test
  public void removeCallsOnDeleteWhenKeyExists() {
    map.put("key", 10);

    map.remove("key");

    assertThat(onDelete.deleted).isEqualTo(10);
  }

  @Test
  public void putAllCallsOnAddForItemsThatAreNewAndChangedForItemsExisting() {
    map.put("existing1", 1);
    map.put("existing3", 3);
    onNew.addedAll.clear();

    Map<String, Integer> putting = new LinkedHashMap<>();
    putting.put("existing1", 11);
    putting.put("missing2", 22);
    putting.put("existing3", 33);
    putting.put("missing4", 44);

    map.putAll(putting);

    assertThat(onChange.currentAll).isEqualTo(Arrays.asList(1, 3));
    assertThat(onChange.replacementAll).isEqualTo(Arrays.asList(11, 33));
    assertThat(onNew.addedAll).isEqualTo(Arrays.asList(22, 44));
  }

  private static class CaptureAdd implements Function<Integer, Integer> {
    private Integer added;
    private List<Integer> addedAll = new LinkedList<>();

    @Override
    public Integer apply(Integer value) {
      this.added = value;
      addedAll.add(value);
      return value;
    }
  }

  private static class CaptureChange implements BiFunction<Integer, Integer, Integer> {
    private Integer current;
    private Integer replacement;
    private List<Integer> currentAll = new LinkedList<>();
    private List<Integer> replacementAll = new LinkedList<>();

    @Override
    public Integer apply(Integer current, Integer replacement) {
      this.current = current;
      this.replacement = replacement;
      currentAll.add(current);
      replacementAll.add(replacement);
      return replacement;
    }
  }

  private static class CaptureDelete implements Consumer<Integer> {
    private Integer deleted;
    private List<Integer> allDeleted = new LinkedList<>();

    @Override
    public void accept(Integer value) {
      this.deleted = value;
      allDeleted.add(value);
    }
  }

}
