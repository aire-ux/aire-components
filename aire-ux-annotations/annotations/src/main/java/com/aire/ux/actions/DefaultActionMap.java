package com.aire.ux.actions;

import io.sunshower.gyre.ArrayIterator;
import io.sunshower.gyre.CompactTrieMap;
import io.sunshower.gyre.TrieMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import lombok.val;
@ThreadSafe
public class DefaultActionMap implements ActionMap {

  final TrieMap<Key, Action> keys;

  public DefaultActionMap() {
    keys = new CompactTrieMap<>(key -> new ArrayIterator<>(key.id().split("\\.")));
  }

  @Override
  @GuardedBy("keys")
  public Action remove(Key key) {
    synchronized (keys) {
      return keys.remove(key);
    }
  }

  @Override
  @GuardedBy("keys")
  public void add(Action action) {
    synchronized (keys) {
      keys.put(action.getKey(), action);
    }
  }

  @Override
  public List<Action> getKeysIn(Key... path) {
    synchronized (keys) {
      val spath = Arrays.stream(path).map(Key::id).collect(Collectors.joining("."));
      val key = Key.of(spath);
      return keys.level(key);
    }
  }

  @Override
  public Action get(Key key) {
    synchronized (keys) {
      return keys.get(key);
    }
  }
}
