/* IndexedMap - (c) 2014, Kieron Wilkinson */

package org.softpres.indexedmap;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Implementation of {@link ReadWriteLock} that doesn't do any locking.
 */
public class NoReadWriteLock implements ReadWriteLock {

  private final Lock noLock = new NoLock();

  @Override
  public Lock readLock() {
    return noLock;
  }

  @Override
  public Lock writeLock() {
    return noLock;
  }

  private static class NoLock implements Lock {
    @Override
    public void lock() {
    }

    @Override
    public void lockInterruptibly() {
    }

    @Override
    public boolean tryLock() {
      return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
      return true;
    }

    @Override
    public void unlock() {
    }

    @Override
    public Condition newCondition() {
      return new NoCondition();
    }
  }

  private static class NoCondition implements Condition {
    @Override
    public void await() {
    }

    @Override
    public void awaitUninterruptibly() {
    }

    @Override
    public long awaitNanos(long nanosTimeout) {
      return 0;
    }

    @Override
    public boolean await(long time, TimeUnit unit) {
      return false;
    }

    @Override
    public boolean awaitUntil(Date deadline) {
      return false;
    }

    @Override
    public void signal() {
    }

    @Override
    public void signalAll() {
    }
  }

}
