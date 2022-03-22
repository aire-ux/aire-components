package io.sunshower.zephyr.ui.components;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.NonNull;

public interface WizardModel<E> {

  static <E extends Enum<E>> WizardModel<E> newModel(Class<E> type) {
    return new DefaultWizardModel<>(new EnumMap<>(type));
  }

  static <E> WizardModel<E> newModel() {
    return new DefaultWizardModel<E>(new HashMap<>());
  }

  <T> T get(E stage, Object key);

  Map<Object, Object> get(E stage);

  void putAll(E stage, Map<Object, Object> values);

  void put(E stage, Object key, Object value);

  <T> T remove(E stage, Object key);

  Map<Object, Object> clear(E key);

  Set<Entry<E, Map<Object, Object>>> values();
}

class DefaultWizardModel<E> implements WizardModel<E> {

  private final Map<E, Map<Object, Object>> pageMap;

  DefaultWizardModel(@NonNull Map<E, Map<Object, Object>> pageMap) {
    this.pageMap = pageMap;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(E stage, Object key) {
    return (T) get(stage).get(key);
  }

  @Override
  public Map<Object, Object> get(E stage) {
    return pageMap.computeIfAbsent(stage, e -> new HashMap<>());
  }

  @Override
  public void putAll(E stage, Map<Object, Object> values) {
    get(stage).putAll(values);
  }

  @Override
  public void put(E stage, Object key, Object value) {
    get(stage).put(key, value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T remove(E stage, Object key) {
    return (T) get(stage).remove(key);
  }

  @Override
  public Map<Object, Object> clear(E key) {
    return pageMap.remove(key);
  }

  @Override
  public Set<Entry<E, Map<Object, Object>>> values() {
    return pageMap.entrySet();
  }
}
