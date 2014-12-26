/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import org.junit.Test;

import java.util.HashMap;
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
  private final ManagedMap<String, Integer> map = new ManagedMap<>(
        new HashMap<>(), onNew, onChange, onDelete);

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


  private static class CaptureAdd implements Function<Integer, Integer> {
    private Integer added;

    @Override
    public Integer apply(Integer value) {
      this.added = value;
      return value;
    }
  }

  private static class CaptureChange implements BiFunction<Integer, Integer, Integer> {
    private Integer current;
    private Integer replacement;

    @Override
    public Integer apply(Integer current, Integer replacement) {
      this.current = current;
      this.replacement = replacement;
      return replacement;
    }
  }

  private class CaptureDelete implements Consumer<Integer> {
    private Integer deleted;

    @Override
    public void accept(Integer value) {
      this.deleted = value;
    }
  }

}
