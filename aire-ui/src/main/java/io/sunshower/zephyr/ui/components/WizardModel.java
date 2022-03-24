package io.sunshower.zephyr.ui.components;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public interface WizardModel<E> {

  static <E> WizardModel<E> newModel() {
    return new DefaultWizardModel<>(new HashMap<>());
  }

  static <E extends Enum<E>> WizardModel<E> newModel(Class<E> type) {
    return new DefaultWizardModel<>(new EnumMap<>(type));
  }

  void set(E key, Object value);
}

class DefaultWizardModel<E> implements WizardModel<E> {

  private final Map<E, Object> model;

  public DefaultWizardModel(Map<E, Object> model) {
    this.model = requireNonNull(model);
  }

  @Override
  public void set(E key, Object value) {
    this.model.put(key, value);
  }
}
