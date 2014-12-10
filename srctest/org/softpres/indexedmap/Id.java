package org.softpres.indexedmap;

/**
 * Animal id.
 */
class Id {

  private final int value;

  Id(int value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Id id = (Id) o;
    return value == id.value;
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

}
