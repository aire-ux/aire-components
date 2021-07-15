package com.aire.ux.test.web;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class LinkedCaseInsensitiveMap<V> extends LinkedHashMap<String, V> implements
    Map<String, V> {

  final Locale locale;

  public LinkedCaseInsensitiveMap(int size, Locale locale) {
    super(size);
    this.locale = locale;
  }

  public LinkedCaseInsensitiveMap() {
    super();
    this.locale = Locale.ENGLISH;
  }
  @Override
  public V get(Object key) {
    if (key == null) {
      return super.get(null);
    }
    if (key instanceof String) {
      return super.get(((String) key).toLowerCase(locale));
    }
    throw new IllegalArgumentException("Idk what this is: " + key);
  }

  @Override
  public V put(String key, V value) {
    if (key == null) {
      return super.put(null, value);
    }
    return super.put(key.toLowerCase(locale), value);
  }
}
