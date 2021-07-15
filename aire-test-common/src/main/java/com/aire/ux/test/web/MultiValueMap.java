package com.aire.ux.test.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.val;

public class MultiValueMap<K, V> extends LinkedHashMap<K, List<V>> {


  public void add(K key, V value) {
    computeIfAbsent(key, k -> new ArrayList<>()).add(value);
  }

  public void addAll(K key, Collection<? extends V> values) {
    computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
  }

  public void addIfAbsent(K key, V value) {
    if (!containsKey(key)) {
      add(key, value);
    }
  }


  public V getFirst(K key) {
    val items = get(key);
    if (!(items == null || items.isEmpty())) {
      return items.get(0);
    }
    return null;
  }

  public void set(K key, V value) {
    val results = get(key);
    if(results == null) {
      add(key, value);
    }
    else if(results.isEmpty()) {
      results.add(value);
    } else {
      results.clear();
      results.add(value);
    }
  }

  public void setAll(Map<K, V> values) {
    for(val kv : values.entrySet()) {
      add(kv.getKey(), kv.getValue());
    }
  }

  public Map<K, V> toSingleValueMap() {
    val results = new LinkedHashMap<K, V>();
    for(val kv : entrySet()) {
      results.put(kv.getKey(), getFirst(kv.getKey()));
    }
    return results;
  }
}
