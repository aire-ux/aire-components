package io.sunshower.zephyr.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Debouncer<T> {

  private final int interval;
  private final Consumer<T> callback;
  private final ScheduledExecutorService scheduler;
  private final ConcurrentHashMap<T, TimerTask> tasks;


  public Debouncer(Consumer<T> c, int interval) {
    this.callback = c;
    this.interval = interval;
    this.tasks = new ConcurrentHashMap<>();
    this.scheduler = Executors.newScheduledThreadPool(1);
  }

  public void call(T key) {
    TimerTask task = new TimerTask(key);

    TimerTask prev;
    do {
      prev = tasks.putIfAbsent(key, task);
      if (prev == null) {
        scheduler.schedule(task, interval, TimeUnit.MILLISECONDS);
      }
    } while (prev != null && !prev.extend());
  }

  public void terminate() {
    scheduler.shutdownNow();
  }

  private class TimerTask implements Runnable {

    private final T key;
    private final Object lock = new Object();
    private long dueTime;

    public TimerTask(T key) {
      this.key = key;
      extend();
    }

    public boolean extend() {
      synchronized (lock) {
        if (dueTime < 0) {
          return false;
        }
        dueTime = System.currentTimeMillis() + interval;
        return true;
      }
    }

    public void run() {
      synchronized (lock) {
        long remaining = dueTime - System.currentTimeMillis();
        if (remaining > 0) { // Re-schedule task
          scheduler.schedule(this, remaining, TimeUnit.MILLISECONDS);
        } else {
          dueTime = -1;
          try {
            callback.accept(key);
          } finally {
            tasks.remove(key);
          }
        }
      }
    }
  }
}