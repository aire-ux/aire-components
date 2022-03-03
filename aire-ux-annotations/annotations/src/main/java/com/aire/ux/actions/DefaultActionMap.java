package com.aire.ux.actions;

import io.sunshower.gyre.ArrayIterator;
import io.sunshower.gyre.CompactTrieMap;
import io.sunshower.gyre.TrieMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;

public class DefaultActionMap implements ActionMap {

  final TrieMap<Key, Action> keys;

  public DefaultActionMap() {
    keys = new CompactTrieMap<>(
        key -> new ArrayIterator<>(key.id().split("\\.")));
  }

  @Override
  public Action remove(Key key) {
    return keys.remove(key);
  }

  @Override
  public void add(Action action) {
    keys.put(action.getKey(), action);
  }

  @Override
  public List<Action> getKeysIn(Key... path) {
    val spath = Arrays.stream(path).map(Key::id).collect(Collectors.joining("."));
    val key = Key.of(spath);
    return keys.level(key);
  }

  @Override
  public Action get(Key key) {
    return keys.get(key);
  }
}
