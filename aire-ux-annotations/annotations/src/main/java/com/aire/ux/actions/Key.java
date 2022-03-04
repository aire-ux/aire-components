package com.aire.ux.actions;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.val;

public interface Key extends Comparable<Key> {

  Key BULK = of("BULK_ACTION_EVENT");

  static Key of(String s) {
    return new DefaultKey(s);
  }

  static Key of(String fst, String... rest) {
    val results = new ArrayList<String>();
    results.add(fst);
    results.addAll(List.of(rest));
    return new DefaultKey(String.join(".", results));
  }

  String id();
}

record DefaultKey(String key) implements Key {

  @Override
  public String id() {
    return key;
  }

  @Override
  public int compareTo(@NonNull Key key) {
    return key.id().compareTo(key.id());
  }
}
